package io.github.wulkanowy.ui.main.grades;

import android.view.View;
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
import io.github.wulkanowy.data.db.dao.entities.Subject;

public class GradesSummarySubItem
        extends AbstractSectionableItem<GradesSummarySubItem.SubItemViewHolder, GradesSummaryHeader> {

    private Subject subject;

    public GradesSummarySubItem(GradesSummaryHeader header, Subject subject) {
        super(header);
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GradesSummarySubItem that = (GradesSummarySubItem) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .toHashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grades_summary_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, SubItemViewHolder holder, int position, List<Object> payloads) {
        holder.onBind(subject);
    }

    static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.grades_summary_subitem_final_grade)
        TextView finalGrade;

        @BindView(R.id.grades_summary_subitem_predicted_grade)
        TextView predictedGrade;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

        void onBind(Subject item) {
            predictedGrade.setText(item.getPredictedRating());
            finalGrade.setText(item.getFinalRating());
        }
    }
}
