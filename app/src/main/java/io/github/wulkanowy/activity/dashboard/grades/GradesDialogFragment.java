package io.github.wulkanowy.activity.dashboard.grades;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.wulkanowy.R;

public class GradesDialogFragment extends DialogFragment {

    private GradeItem grade;

    public static final GradesDialogFragment newInstance(GradeItem grade) {
        return new GradesDialogFragment().setGrade(grade);
    }

    public GradesDialogFragment setGrade(GradeItem grade) {
        this.grade = grade;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grades_dialog, container, false);

        TextView gradeText = (TextView) view.findViewById(R.id.dialog_grade_text);
        TextView subjectText = (TextView) view.findViewById(R.id.subject_dialog_text_value);
        TextView descriptionText = (TextView) view.findViewById(R.id.description_dialog_text_value);
        TextView weightText = (TextView) view.findViewById(R.id.weight_dialog_text_value);
        TextView teacherText = (TextView) view.findViewById(R.id.teacher_dialog_text_value);
        TextView dateText = (TextView) view.findViewById(R.id.date_dialog_text_value);
        TextView colorText = (TextView) view.findViewById(R.id.color_dialog_text_value);
        TextView okTextClick = (TextView) view.findViewById(R.id.OK_dialog);

        subjectText.setText(grade.getSubject());
        gradeText.setText(grade.getValue());
        gradeText.setBackgroundResource(grade.getValueColor());
        weightText.setText(grade.getWeight());
        dateText.setText(grade.getDate());
        colorText.setText(colorHexToColorName(grade.getColor()));

        if (grade.getDescription().equals("")) {
            if (!grade.getSymbol().equals("")) {
                descriptionText.setText(grade.getSymbol());
            }
        } else if (!grade.getSymbol().equals("")) {
            descriptionText.setText(grade.getSymbol() + " - " + grade.getDescription());
        } else {
            descriptionText.setText(grade.getDescription());
        }

        if (!grade.getTeacher().equals("")) {
            teacherText.setText(grade.getTeacher());
        }

        okTextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public int colorHexToColorName(String hexColor) {
        switch (hexColor) {
            case "000000": {
                return R.string.color_black_text;
            }
            case "F04C4C": {
                return R.string.color_red_text;
            }
            case "20A4F7": {
                return R.string.color_blue_text;
            }
            case "6ECD07": {
                return R.string.color_green_text;
            }
            default: {
                return R.string.noColor_text;
            }

        }
    }
}
