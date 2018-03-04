package io.github.wulkanowy.ui.main.timetable;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Lesson;


public class TimetableSubItem
        extends AbstractSectionableItem<TimetableSubItem.SubItemViewHolder, TimetableHeaderItem> {

    private Lesson lesson;

    public TimetableSubItem(TimetableHeaderItem header, Lesson lesson) {
        super(header);
        this.lesson = lesson;
    }

    public Lesson getLesson() {
        return lesson;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.timetable_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SubItemViewHolder holder, int position, List payloads) {
        holder.onBind(lesson);
    }

    static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.timetable_subItem_lesson)
        TextView lessonName;

        @BindView(R.id.timetable_subItem_number_of_lesson)
        TextView numberOfLesson;

        @BindView(R.id.timetable_subItem_time)
        TextView lessonTime;

        @BindView(R.id.timetable_subItem_room)
        TextView room;

        @BindView(R.id.timetable_subItem_alert_image)
        ImageView alert;

        private Context context;

        private Lesson item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        void onBind(Lesson lesson) {
            item = lesson;

            lessonName.setText(lesson.getSubject());
            lessonTime.setText(getLessonTimeString());
            numberOfLesson.setText(lesson.getNumber());
            room.setText(getRoomString());
            alert.setVisibility(lesson.getIsMovedOrCanceled() || lesson.getIsNewMovedInOrChanged()
                    ? View.VISIBLE : View.INVISIBLE);
            lessonName.setPaintFlags(lesson.getIsMovedOrCanceled() ? lessonName.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG :
                    lessonName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            room.setText(getRoomString());
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();
        }

        private String getLessonTimeString() {
            return String.format("%1$s - %2$s", item.getStartTime(), item.getEndTime());
        }

        private String getRoomString() {
            if (!item.getRoom().isEmpty()) {
                return context.getString(R.string.timetable_subitem_room, item.getRoom());
            } else {
                return item.getRoom();
            }
        }

        private void showDialog() {
            TimetableDialogFragment dialogFragment = TimetableDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), item.toString());
        }
    }
}
