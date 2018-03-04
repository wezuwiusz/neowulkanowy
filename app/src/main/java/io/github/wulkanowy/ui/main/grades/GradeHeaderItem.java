package io.github.wulkanowy.ui.main.grades;


import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.utils.AverageCalculator;

public class GradeHeaderItem
        extends AbstractExpandableHeaderItem<GradeHeaderItem.HeaderViewHolder, GradesSubItem> {

    private Subject subject;

    GradeHeaderItem(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GradeHeaderItem that = (GradeHeaderItem) o;

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
        return R.layout.grade_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        holder.onBind(subject, getSubItems());
    }

    static class HeaderViewHolder extends ExpandableViewHolder {

        @BindView(R.id.grade_header_subject_text)
        TextView subjectName;

        @BindView(R.id.grade_header_average_text)
        TextView averageText;

        @BindView(R.id.grade_header_number_of_grade_text)
        TextView numberText;

        @BindView(R.id.grade_header_alert_image)
        View alertImage;

        Resources resources;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            resources = view.getResources();
            view.setOnClickListener(this);
        }

        void onBind(Subject item, List<GradesSubItem> subItems) {
            subjectName.setText(item.getName());
            numberText.setText(resources.getQuantityString(R.plurals.numberOfGradesPlurals,
                    subItems.size(), subItems.size()));
            averageText.setText(getGradesAverageString(item));
            alertImage.setVisibility(isSubItemsRead(subItems) ? View.INVISIBLE : View.VISIBLE);
            alertImage.setTag(item.getName());
        }

        private boolean isSubItemsRead(List<GradesSubItem> subItems) {
            boolean isRead = true;

            for (GradesSubItem item : subItems) {
                isRead = item.getGrade().getRead();
            }
            return isRead;
        }

        private String getGradesAverageString(Subject item) {
            float average = AverageCalculator.calculate(item.getGradeList());

            if (average < 0) {
                return resources.getString(R.string.info_no_average);
            } else {
                return resources.getString(R.string.info_average_grades, average);
            }
        }
    }
}
