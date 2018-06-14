package io.github.wulkanowy.ui.main.attendance;

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
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;

public class AttendanceDialogFragment extends DialogFragment {

    private static final String ARGUMENT_KEY = "Item";

    private AttendanceLesson lesson;

    @BindView(R.id.attendance_dialog_subject_value)
    TextView subject;

    @BindView(R.id.attendance_dialog_date_value)
    TextView date;

    @BindView(R.id.attendance_dialog_number_value)
    TextView number;

    @BindView(R.id.attendance_dialog_description_value)
    TextView description;

    public AttendanceDialogFragment() {
        //empty constructor for fragment
    }

    public static AttendanceDialogFragment newInstance(AttendanceLesson lesson) {
        AttendanceDialogFragment dialogFragment = new AttendanceDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_KEY, lesson);

        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lesson = (AttendanceLesson) getArguments().getSerializable(ARGUMENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance_dialog, container, false);

        ButterKnife.bind(this, view);

        if (!lesson.getSubject().isEmpty()) {
            subject.setText(lesson.getSubject());
        }

        if (!lesson.getDate().isEmpty()) {
            date.setText(lesson.getDate());
        }

        if (0 != lesson.getNumber()) {
            number.setText(String.valueOf(lesson.getNumber()));
        }

        description.setText(lesson.getDescription());

        if (lesson.getAbsenceUnexcused()) {
            description.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        return view;
    }

    @OnClick(R.id.attendance_dialog_close)
    void onClickCloseButton() {
        dismiss();
    }
}
