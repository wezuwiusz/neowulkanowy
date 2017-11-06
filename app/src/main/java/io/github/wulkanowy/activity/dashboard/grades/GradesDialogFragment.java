package io.github.wulkanowy.activity.dashboard.grades;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Grade;

public class GradesDialogFragment extends DialogFragment {

    private Grade grade;

    public static final GradesDialogFragment newInstance(Grade grade) {
        return new GradesDialogFragment().setGrade(grade);
    }

    public GradesDialogFragment() {
        setRetainInstance(true);
    }

    public GradesDialogFragment setGrade(Grade grade) {
        this.grade = grade;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grades_dialog, container, false);

        TextView gradeText = view.findViewById(R.id.dialog_grade_text);
        TextView subjectText = view.findViewById(R.id.subject_dialog_text_value);
        TextView descriptionText = view.findViewById(R.id.description_dialog_text_value);
        TextView weightText = view.findViewById(R.id.weight_dialog_text_value);
        TextView teacherText = view.findViewById(R.id.teacher_dialog_text_value);
        TextView dateText = view.findViewById(R.id.date_dialog_text_value);
        TextView colorText = view.findViewById(R.id.color_dialog_text_value);
        Button closeDialog = view.findViewById(R.id.close_dialog);

        subjectText.setText(grade.getSubject());
        gradeText.setText(grade.getValue());
        gradeText.setBackgroundResource(grade.getValueColor());
        weightText.setText(grade.getWeight());
        dateText.setText(grade.getDate());
        colorText.setText(colorHexToColorName(grade.getColor()));

        if ("".equals(grade.getDescription())) {
            if (!"".equals(grade.getSymbol())) {
                descriptionText.setText(grade.getSymbol());
            }
        } else if (!"".equals(grade.getSymbol())) {
            descriptionText.setText(String.format("%1$s - %2$s", grade.getSymbol(), grade.getDescription()));
        } else {
            descriptionText.setText(grade.getDescription());
        }

        if (!"".equals(grade.getTeacher())) {
            teacherText.setText(grade.getTeacher());
        }

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    public static int colorHexToColorName(String hexColor) {
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
