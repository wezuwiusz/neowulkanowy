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
import io.github.wulkanowy.utils.AnimationUtils;
import io.github.wulkanowy.utils.GradeUtils;

public class GradeHeaderItem
        extends AbstractExpandableHeaderItem<GradeHeaderItem.HeaderViewHolder, GradesSubItem> {

    private Subject subject;

    private final boolean isShowSummary;

    GradeHeaderItem(Subject subject, boolean isShowSummary) {
        this.subject = subject;
        this.isShowSummary = isShowSummary;
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
        return new HeaderViewHolder(view, adapter, isShowSummary);
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

        @BindView(R.id.grade_header_predicted_rating_text)
        TextView predictedTest;

        @BindView(R.id.grade_header_final_rating_text)
        TextView finalText;

        @BindView(R.id.grade_header_alert_image)
        View alertImage;

        Resources resources;

        private boolean isSummaryTogglable = true;

        private boolean isShowSummary;

        HeaderViewHolder(View view, FlexibleAdapter adapter, boolean isShowSummary) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            resources = view.getResources();
            view.setOnClickListener(this);
            this.isShowSummary = isShowSummary;
        }

        void onBind(Subject item, List<GradesSubItem> subItems) {
            subjectName.setText(item.getName());
            numberText.setText(resources.getQuantityString(R.plurals.numberOfGradesPlurals,
                    subItems.size(), subItems.size()));
            averageText.setText(getGradesAverageString(item));

            predictedTest.setText(resources.getString(R.string.info_grades_predicted_rating,
                    item.getPredictedRating()));
            finalText.setText(resources.getString(R.string.info_grades_final_rating,
                    item.getFinalRating()));
            predictedTest.setVisibility(View.GONE);
            finalText.setVisibility(View.GONE);

            boolean isSummaryEmpty = true;

            if (!"-".equals(item.getPredictedRating()) || !"-".equals(item.getFinalRating())) {
                isSummaryEmpty = false;
            }

            if (isSummaryEmpty) {
                isSummaryTogglable = false;
            } else if (isShowSummary) {
                predictedTest.setVisibility(View.VISIBLE);
                finalText.setVisibility(View.VISIBLE);

                isSummaryTogglable = false;
            }

            alertImage.setVisibility(isSubItemsReadAndSaveAlertView(subItems)
                    ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            if (subjectName.getLineCount() == 1) {
                subjectName.setMaxLines(3);
            } else {
                subjectName.setMaxLines(1);
            }

            if (isSummaryTogglable) {
                toggleText(predictedTest);
                toggleText(finalText);
            }
        }

        private void toggleText(final View view) {
            if (view.getVisibility() == View.GONE) {
                AnimationUtils.slideDown(view);
            } else {
                AnimationUtils.slideUp(view);
            }
        }

        private boolean isSubItemsReadAndSaveAlertView(List<GradesSubItem> subItems) {
            boolean isRead = true;

            for (GradesSubItem item : subItems) {
                isRead = item.getGrade().getRead();
                item.setSubjectAlertImage(alertImage);
            }

            return isRead;
        }

        private String getGradesAverageString(Subject item) {
            float average = GradeUtils.calculate(item.getGradeList());

            if (average < 0) {
                return resources.getString(R.string.info_no_average);
            }

            return resources.getString(R.string.info_average_grades, average);
        }
    }
}
