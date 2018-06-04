package io.github.wulkanowy.ui.main.attendance.tab;

import android.content.Context;
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
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.ui.main.attendance.AttendanceDialogFragment;

class AttendanceSubItem
        extends AbstractSectionableItem<AttendanceSubItem.SubItemViewHolder, AttendanceHeader> {

    private AttendanceLesson lesson;

    AttendanceSubItem(AttendanceHeader header, AttendanceLesson lesson) {
        super(header);
        this.lesson = lesson;
    }

    AttendanceLesson getLesson() {
        return lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttendanceSubItem that = (AttendanceSubItem) o;

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
        return R.layout.attendance_subitem;
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

        @BindView(R.id.attendance_subItem_lesson)
        TextView lessonName;

        @BindView(R.id.attendance_subItem_number)
        TextView lessonNumber;

        @BindView(R.id.attendance_subItem_description)
        TextView lessonDescription;

        @BindView(R.id.attendance_subItem_alert_image)
        ImageView alert;

        private Context context;

        private AttendanceLesson item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        void onBind(AttendanceLesson lesson) {
            item = lesson;

            lessonName.setText(lesson.getSubject());
            lessonNumber.setText((String.valueOf(lesson.getNumber())));
            lessonDescription.setText(lesson.getDescription());
            alert.setVisibility(lesson.getAbsenceUnexcused() || lesson.getUnexcusedLateness()
                    ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();
        }

        private void showDialog() {
            AttendanceDialogFragment dialogFragment = AttendanceDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), item.toString());
        }
    }
}
