package io.github.wulkanowy.ui.main.grades;

import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Subject;

class GradesSummaryHeader extends AbstractHeaderItem<GradesSummaryHeader.HeaderViewHolder> {

    private Subject subject;

    private String average;

    GradesSummaryHeader(Subject subject, float average) {
        this.subject = subject;
        this.average = String.format(Locale.FRANCE, "%.2f", average);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GradesSummaryHeader that = (GradesSummaryHeader) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(average, that.average)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(average)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grades_summary_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HeaderViewHolder holder, int position, List<Object> payloads) {
        holder.onBind(subject, average);
    }

    static class HeaderViewHolder extends FlexibleViewHolder {

        @BindView(R.id.grades_summary_header_name)
        TextView name;

        @BindView(R.id.grades_summary_header_average)
        TextView average;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        void onBind(Subject item, String value) {
            name.setText(item.getName());
            average.setText("-1,00".equals(value) ? "" : value);
        }
    }
}
