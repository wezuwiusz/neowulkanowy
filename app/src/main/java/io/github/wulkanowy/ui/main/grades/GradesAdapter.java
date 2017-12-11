package io.github.wulkanowy.ui.main.grades;


import android.app.Activity;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.utilities.AverageCalculator;

public class GradesAdapter extends ExpandableRecyclerViewAdapter<GradesAdapter.SubjectViewHolder, GradesAdapter.GradeViewHolder> {

    private static int numberOfNotReadGrade;

    private Activity activity;

    public GradesAdapter(List<? extends ExpandableGroup> groups, Activity activity) {
        super(groups);
        this.activity = activity;
    }

    @Override
    public SubjectViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view, activity);
    }

    @Override
    public GradeViewHolder onCreateChildViewHolder(ViewGroup child, int viewType) {
        View view = LayoutInflater.from(child.getContext()).inflate(R.layout.grade_item, child, false);
        return new GradeViewHolder(view, activity);
    }

    @Override
    public void onBindGroupViewHolder(SubjectViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind(group);
    }

    @Override
    public void onBindChildViewHolder(GradeViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.bind((Grade) group.getItems().get(childIndex));
    }

    public static class SubjectViewHolder extends GroupViewHolder {

        private WeakReference<Activity> activity;

        private TextView subjectName;

        private TextView numberOfGrades;

        private TextView averageGrades;

        private ImageView subjectAlertNewGrades;

        public SubjectViewHolder(View itemView, Activity activity) {
            super(itemView);
            this.activity = new WeakReference<>(activity);

            subjectName = itemView.findViewById(R.id.subject_text);
            numberOfGrades = itemView.findViewById(R.id.subject_number_of_grades);
            subjectAlertNewGrades = itemView.findViewById(R.id.subject_new_grades_alert);
            averageGrades = itemView.findViewById(R.id.subject_grades_average);
        }

        @SuppressWarnings("unchecked")
        public void bind(ExpandableGroup group) {
            int volumeGrades = group.getItemCount();
            List<Grade> gradeList = group.getItems();
            float average = AverageCalculator.calculate(gradeList);

            itemView.setTag(group.getTitle());

            if (average < 0) {
                averageGrades.setText(R.string.info_no_average);
            } else {
                averageGrades.setText(activity.get().getResources().getString(R.string.info_average_grades, average));
            }
            subjectName.setText(group.getTitle());
            numberOfGrades.setText(activity.get().getResources().getQuantityString(R.plurals.numberOfGradesPlurals, volumeGrades, volumeGrades));

            for (Grade grade : gradeList) {
                if (!grade.getRead()) {
                    subjectAlertNewGrades.setVisibility(View.VISIBLE);
                } else {
                    subjectAlertNewGrades.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public static class GradeViewHolder extends ChildViewHolder {

        private WeakReference<Activity> activity;

        private TextView gradeValue;

        private TextView descriptionGrade;

        private TextView dateGrade;

        private ImageView alertNewGrade;

        private View itemView;

        private Grade gradeItem;

        public GradeViewHolder(View itemView, Activity activity) {
            super(itemView);
            this.itemView = itemView;
            this.activity = new WeakReference<>(activity);

            gradeValue = itemView.findViewById(R.id.grade_text);
            descriptionGrade = itemView.findViewById(R.id.description_grade_text);
            dateGrade = itemView.findViewById(R.id.grade_date_text);
            alertNewGrade = itemView.findViewById(R.id.grade_new_grades_alert);
        }

        public void bind(Grade grade) {
            gradeValue.setText(grade.getValue());
            gradeValue.setBackgroundResource(grade.getValueColor());
            dateGrade.setText(grade.getDate());
            gradeItem = grade;

            if (grade.getDescription() == null || "".equals(grade.getDescription())) {
                if (!"".equals(grade.getSymbol())) {
                    descriptionGrade.setText(grade.getSymbol());
                } else {
                    descriptionGrade.setText(R.string.noDescription_text);
                }
            } else {
                descriptionGrade.setText(grade.getDescription());
            }

            if (gradeItem.getRead()) {
                alertNewGrade.setVisibility(View.INVISIBLE);
            } else {
                alertNewGrade.setVisibility(View.VISIBLE);
                numberOfNotReadGrade++;
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GradesDialogFragment gradesDialogFragment = GradesDialogFragment.newInstance(gradeItem);
                    gradesDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    gradesDialogFragment.show(activity.get().getFragmentManager(), gradeItem.toString());

                    if (!gradeItem.getRead()) {
                        numberOfNotReadGrade--;
                    }

                    if (numberOfNotReadGrade == 0) {
                        View subjectView = activity.get().findViewById(R.id.subject_grade_recycler).findViewWithTag(gradeItem.getSubject());
                        if (subjectView != null) {
                            View subjectAlertNewGrade = subjectView.findViewById(R.id.subject_new_grades_alert);
                            subjectAlertNewGrade.setVisibility(View.INVISIBLE);
                        }
                    }

                    gradeItem.setRead(true);
                    gradeItem.setIsNew(false);
                    gradeItem.update();
                    alertNewGrade.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}
