package io.github.wulkanowy.ui.main.grades;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.utils.CommonUtils;

public class GradesDialogFragment extends DialogFragment {

    private static final String ARGUMENT_KEY = "Item";

    private Grade grade;

    @BindView(R.id.grade_dialog_value)
    TextView value;

    @BindView(R.id.grade_dialog_subject)
    TextView subject;

    @BindView(R.id.grade_dialog_description_value)
    TextView description;

    @BindView(R.id.grade_dialog_weight_value)
    TextView weight;

    @BindView(R.id.grade_dialog_teacher_value)
    TextView teacher;

    @BindView(R.id.grade_dialog_color_value)
    TextView color;

    @BindView(R.id.grade_dialog_date_value)
    TextView date;

    public GradesDialogFragment() {
        //empty constructor for fragment
    }

    public static GradesDialogFragment newInstance(Grade item) {
        GradesDialogFragment dialogFragment = new GradesDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_KEY, item);

        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            grade = (Grade) getArguments().getSerializable(ARGUMENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grade_dialog, container, false);

        ButterKnife.bind(this, view);

        subject.setText(grade.getSubject());
        value.setText(grade.getValue());
        value.setBackgroundResource(grade.getValueColor());
        weight.setText(grade.getWeight());
        date.setText(grade.getDate());
        color.setText(CommonUtils.colorHexToColorName(grade.getColor()));
        teacher.setText(getTeacherString());
        description.setText(getDescriptionString());


        return view;
    }

    @OnClick(R.id.grade_dialog_close_button)
    void onClickClose() {
        dismiss();
    }

    private String getDescriptionString() {
        if ("".equals(grade.getDescription())) {
            if (!"".equals(grade.getSymbol())) {
                return grade.getSymbol();
            } else {
                return getString(R.string.noDescription_text);
            }
        } else if (!"".equals(grade.getSymbol())) {
            return String.format("%1$s - %2$s", grade.getSymbol(), grade.getDescription());
        } else {
            return grade.getDescription();
        }
    }

    private String getTeacherString() {
        if (grade.getTeacher() != null && !"".equals(grade.getTeacher())) {
            return grade.getTeacher();
        } else {
            return getString(R.string.generic_app_no_data);
        }
    }
}
