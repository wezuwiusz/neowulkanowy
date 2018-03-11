package io.github.wulkanowy.ui.main.attendance;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Day;

public class AttendanceHeaderItem
        extends AbstractExpandableHeaderItem<AttendanceHeaderItem.HeaderViewHolder, AttendanceSubItem> {

    private Day day;

    AttendanceHeaderItem(Day day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttendanceHeaderItem that = (AttendanceHeaderItem) o;

        return new EqualsBuilder()
                .append(day, that.day)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(day)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        holder.onBind(day, getSubItems());
    }

    static class HeaderViewHolder extends ExpandableViewHolder {

        @BindView(R.id.attendance_header_day)
        TextView dayName;

        @BindView(R.id.attendance_header_date)
        TextView date;

        @BindView(R.id.attendance_header_description)
        TextView description;

        @BindView(R.id.attendance_header_alert_image)
        ImageView alert;

        @BindView(R.id.attendance_header_free_name)
        TextView freeName;

        @BindColor(R.color.secondary_text)
        int secondaryColor;

        @BindColor(R.color.free_day)
        int backgroundFreeDay;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        void onBind(Day item, List<AttendanceSubItem> subItems) {
            dayName.setText(StringUtils.capitalize(item.getDayName()));
            date.setText(item.getDate());

            int numberOfHours = countNotPresentHours(subItems);
            description.setText((getContentView().getResources().getQuantityString(R.plurals.numberOfAbsences,
                    numberOfHours, numberOfHours)));
            description.setVisibility(numberOfHours > 0 ? View.VISIBLE : View.INVISIBLE);
            alert.setVisibility(isSubItemsHasChanges(subItems) ? View.VISIBLE : View.INVISIBLE);
            freeName.setVisibility(subItems.isEmpty() ? View.VISIBLE : View.INVISIBLE);

            if (item.getAttendanceLessons().isEmpty()) {
                ((FrameLayout) getContentView()).setForeground(null);
                getContentView().setBackgroundColor(backgroundFreeDay);
                dayName.setTextColor(secondaryColor);
            }
        }

        private int countNotPresentHours(List<AttendanceSubItem> subItems) {
            int i = 0;
            for (AttendanceSubItem subItem : subItems) {
                if (subItem.getLesson().getIsAbsenceUnexcused()) {
                    i++;
                }
            }

            return i;
        }

        private boolean isSubItemsHasChanges(List<AttendanceSubItem> subItems) {
            for (AttendanceSubItem subItem : subItems) {
                if (subItem.getLesson().getIsAbsenceUnexcused() || subItem.getLesson().getIsUnexcusedLateness()) {
                    return true;
                }
            }

            return false;
        }
    }
}
