package io.github.wulkanowy.ui.main.timetable.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.utils.CommonUtils;

public class TimetableHeader
        extends AbstractExpandableHeaderItem<TimetableHeader.HeaderViewHolder, TimetableSubItem> {

    private Day day;

    TimetableHeader(Day day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimetableHeader that = (TimetableHeader) o;

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
        return R.layout.timetable_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HeaderViewHolder holder,
                               int position, List<Object> payloads) {
        holder.onBind(day, getSubItems());
    }

    static class HeaderViewHolder extends ExpandableViewHolder {

        @BindView(R.id.timetable_header_day)
        TextView dayName;

        @BindView(R.id.timetable_header_date)
        TextView date;

        @BindView(R.id.timetable_header_alert_image)
        ImageView alert;

        @BindView(R.id.timetable_header_free_name)
        TextView freeName;

        private Context context;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        void onBind(Day item, List<TimetableSubItem> subItems) {
            dayName.setText(StringUtils.capitalize(item.getDayName()));
            date.setText(item.getDate());
            alert.setVisibility(isSubItemNewMovedInOrChanged(subItems) ? View.VISIBLE : View.INVISIBLE);
            freeName.setVisibility(item.getFreeDay() ? View.VISIBLE : View.INVISIBLE);
            freeName.setText(item.getFreeDayName());
            setInactiveHeader(item.getFreeDay());
        }

        private void setInactiveHeader(boolean inactive) {
            ((FrameLayout) getContentView()).setForeground(inactive ? null : getSelectableDrawable());
            dayName.setTextColor(CommonUtils.getThemeAttrColor(context,
                    inactive ? android.R.attr.textColorSecondary : android.R.attr.textColorPrimary));

            if (inactive) {
                getContentView().setBackgroundColor(CommonUtils.getThemeAttrColor(context, R.attr.colorControlHighlight));
            } else {
                getContentView().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_border));
            }
        }

        private Drawable getSelectableDrawable() {
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            Drawable drawable = typedArray.getDrawable(0);
            typedArray.recycle();
            return drawable;
        }

        private boolean isSubItemNewMovedInOrChanged(List<TimetableSubItem> subItems) {
            boolean isAlertActive = false;

            for (TimetableSubItem subItem : subItems) {
                if (subItem.getLesson().getMovedOrCanceled() ||
                        subItem.getLesson().getNewMovedInOrChanged()) {
                    isAlertActive = true;
                }
            }
            return isAlertActive;
        }
    }
}
