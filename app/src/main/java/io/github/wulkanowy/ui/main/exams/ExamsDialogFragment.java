package io.github.wulkanowy.ui.main.exams;


import android.os.Bundle;
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
import io.github.wulkanowy.data.db.dao.entities.Exam;

public class ExamsDialogFragment extends DialogFragment {

    private static final String ARGUMENT_KEY = "Item";

    private Exam exam;

    @BindView(R.id.exams_dialog_subject_value)
    TextView subject;

    @BindView(R.id.exams_dialog_type_value)
    TextView type;

    @BindView(R.id.exams_dialog_teacher_value)
    TextView teacher;

    @BindView(R.id.exams_dialog_date_value)
    TextView date;

    @BindView(R.id.exams_dialog_description_value)
    TextView description;

    public static ExamsDialogFragment newInstance(Exam exam) {
        ExamsDialogFragment dialogFragment = new ExamsDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_KEY, exam);

        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exam = (Exam) getArguments().getSerializable(ARGUMENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exams_dialog, container, false);

        ButterKnife.bind(this, view);

        subject.setText(exam.getSubjectAndGroup());
        teacher.setText(exam.getTeacher());
        type.setText(exam.getType());
        date.setText(exam.getDate());

        if (!exam.getDescription().isEmpty()) {
            description.setText(exam.getDescription());
        }

        return view;
    }

    @OnClick(R.id.exams_dialog_close)
    void onClickCloseButton() {
        dismiss();
    }
}
