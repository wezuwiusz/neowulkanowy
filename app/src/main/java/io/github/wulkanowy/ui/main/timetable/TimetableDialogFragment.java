package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Lesson;

public class TimetableDialogFragment extends DialogFragment {

    private static final String ARGUMENT_KEY = "Item";

    private Lesson lesson;

    @BindView(R.id.timetable_dialog_lesson_value)
    TextView lessonName;

    @BindView(R.id.timetable_dialog_teacher_value)
    TextView teacher;

    @BindView(R.id.timetable_dialog_group_value)
    TextView group;

    @BindView(R.id.timetable_dialog_room_value)
    TextView room;

    @BindView(R.id.timetable_dialog_time_value)
    TextView time;

    @BindView(R.id.timetable_dialog_description_value)
    TextView description;

    @BindView(R.id.timetable_dialog_description)
    View descriptionLabel;

    @BindView(R.id.timetable_dialog_teacher)
    View teacherLabel;

    @BindView(R.id.timetable_dialog_group)
    View groupLabel;

    public TimetableDialogFragment() {
        //empty constructor for fragment
    }

    public static TimetableDialogFragment newInstance(Lesson lesson) {
        TimetableDialogFragment dialogFragment = new TimetableDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_KEY, lesson);

        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lesson = (Lesson) getArguments().getSerializable(ARGUMENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_dialog, container, false);

        ButterKnife.bind(this, view);

        if (!lesson.getSubject().isEmpty()) {
            lessonName.setText(lesson.getSubject());
        }

        if (!lesson.getTeacher().isEmpty()) {
            teacher.setText(lesson.getTeacher());
        } else {
            teacher.setVisibility(View.GONE);
            teacherLabel.setVisibility(View.GONE);
        }

        if (!lesson.getGroupName().isEmpty()) {
            group.setText(lesson.getGroupName());
        } else {
            group.setVisibility(View.GONE);
            groupLabel.setVisibility(View.GONE);
        }

        if (!lesson.getRoom().isEmpty()) {
            room.setText(lesson.getRoom());
        }

        if (!lesson.getEndTime().isEmpty() && !lesson.getStartTime().isEmpty()) {
            time.setText(getTimeString());
        }

        if (!lesson.getDescription().isEmpty()) {
            description.setText(StringUtils.capitalize(lesson.getDescription()));
        } else {
            description.setVisibility(View.GONE);
            descriptionLabel.setVisibility(View.GONE);
        }

        return view;
    }

    private String getTimeString() {
        return String.format("%1$s - %2$s", lesson.getStartTime(), lesson.getEndTime());
    }

    @OnClick(R.id.timetable_dialog_close)
    void onClickCloseButton() {
        dismiss();
    }
}
