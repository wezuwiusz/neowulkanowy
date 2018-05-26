package io.github.wulkanowy.ui.main.timetable.tab;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.ui.main.timetable.TimetableDialogFragment;


public class TimetableSubItem
        extends AbstractSectionableItem<TimetableSubItem.SubItemViewHolder, TimetableHeaderItem> {

    private TimetableLesson lesson;

    TimetableSubItem(TimetableHeaderItem header, TimetableLesson lesson) {
        super(header);
        this.lesson = lesson;
    }

    public TimetableLesson getLesson() {
        return lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimetableSubItem that = (TimetableSubItem) o;

        return new EqualsBuilder()
                .append(lesson, that.lesson)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(lesson)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.timetable_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, SubItemViewHolder holder,
                               int position, List<Object> payloads) {
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

        private TimetableLesson item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        void onBind(TimetableLesson lesson) {
            item = lesson;

            lessonName.setText(lesson.getSubject());
            lessonTime.setText(getLessonTimeString());
            numberOfLesson.setText(String.valueOf(lesson.getNumber()));
            room.setText(getRoomString());
            alert.setVisibility(lesson.getMovedOrCanceled() || lesson.getNewMovedInOrChanged()
                    ? View.VISIBLE : View.INVISIBLE);
            lessonName.setPaintFlags(lesson.getMovedOrCanceled() ? lessonName.getPaintFlags()
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
