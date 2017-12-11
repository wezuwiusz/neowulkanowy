package io.github.wulkanowy.ui.main.timetable;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableDialogFragment extends DialogFragment {

    private Lesson lesson;

    public TimetableDialogFragment() {
        //empty constructor for fragment
    }

    public static TimetableDialogFragment newInstance(Lesson lesson) {
        return new TimetableDialogFragment().setLesson(lesson);
    }

    private TimetableDialogFragment setLesson(Lesson lesson) {
        this.lesson = lesson;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_dialog, container, false);
        setRetainInstance(true);

        TextView lessonText = view.findViewById(R.id.timetable_dialog_lesson_value);
        TextView teacherText = view.findViewById(R.id.timetable_dialog_teacher_value);
        TextView groupText = view.findViewById(R.id.timetable_dialog_group_value);
        TextView roomText = view.findViewById(R.id.timetable_dialog_room_value);
        TextView timeText = view.findViewById(R.id.timetable_dialog_time_value);
        TextView descriptionText = view.findViewById(R.id.timetable_dialog_description_value);
        Button closeButton = view.findViewById(R.id.timetable_dialog_close);

        if (!lesson.getSubject().isEmpty()) {
            lessonText.setText(lesson.getSubject());
        }

        if (!lesson.getTeacher().isEmpty()) {
            teacherText.setText(lesson.getTeacher());
        } else {
            teacherText.setVisibility(View.GONE);
            view.findViewById(R.id.timetable_dialog_teacher).setVisibility(View.GONE);
        }

        if (!lesson.getGroupName().isEmpty()) {
            groupText.setText(lesson.getGroupName());
        } else {
            groupText.setVisibility(View.GONE);
            view.findViewById(R.id.timetable_dialog_group).setVisibility(View.GONE);
        }

        if (!lesson.getRoom().isEmpty()) {
            roomText.setText(lesson.getRoom());
        }

        if (!lesson.getEndTime().isEmpty() && !lesson.getStartTime().isEmpty()) {
            timeText.setText(String.format("%1$s - %2$s", lesson.getStartTime(), lesson.getEndTime()));
        }

        if (!lesson.getDescription().isEmpty()) {
            descriptionText.setText(StringUtils.capitalize(lesson.getDescription()));
        } else {
            descriptionText.setVisibility(View.GONE);
            view.findViewById(R.id.timetable_dialog_description).setVisibility(View.GONE);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
