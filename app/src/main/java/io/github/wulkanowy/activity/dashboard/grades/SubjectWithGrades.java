package io.github.wulkanowy.activity.dashboard.grades;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SubjectWithGrades extends ExpandableGroup<GradeItem> {

    public SubjectWithGrades(String title, List<GradeItem> items) {
        super(title, items);
    }
}
