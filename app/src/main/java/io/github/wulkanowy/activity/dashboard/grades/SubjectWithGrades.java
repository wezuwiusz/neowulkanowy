package io.github.wulkanowy.activity.dashboard.grades;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public class SubjectWithGrades extends ExpandableGroup<Grade> {

    public SubjectWithGrades(String title, List<Grade> items) {
        super(title, items);
    }
}
