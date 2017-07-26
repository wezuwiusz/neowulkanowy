package io.github.wulkanowy.activity.dashboard.lessonplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.wulkanowy.R;

public class LessonPlanFragment extends Fragment {

    public LessonPlanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessonplan, container, false);
    }
}
