package io.github.wulkanowy.ui.main.exams;

import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Day;

public class ExamsHeaderItem extends AbstractHeaderItem<ExamsHeaderItem.HeaderVieHolder> {

    private Day day;

    public ExamsHeaderItem(Day day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExamsHeaderItem that = (ExamsHeaderItem) o;

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
        return R.layout.exams_header;
    }

    @Override
    public HeaderVieHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new HeaderVieHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HeaderVieHolder holder, int position, List<Object> payloads) {
        holder.onBind(day);
    }

    static class HeaderVieHolder extends FlexibleViewHolder {

        @BindView(R.id.exams_header_name)
        TextView name;

        @BindView(R.id.exams_header_date)
        TextView date;

        HeaderVieHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        void onBind(Day item) {
            name.setText(StringUtils.capitalize(item.getDayName()));
            date.setText(item.getDate());
        }
    }
}
