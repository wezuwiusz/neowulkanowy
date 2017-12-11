package io.github.wulkanowy.ui.main.grades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public class SubjectWithGradesTest {

    private List<Grade> gradeListEmpty = new ArrayList<>();

    private List<Grade> gradeList = new ArrayList<>();

    private Grade grade = new Grade().setDescription("Lorem ipsum");

    @Before
    public void setUp() {
        gradeList.add(grade);
    }

    @Test
    public void countTest() {
        SubjectWithGrades subjectWithGrades = new SubjectWithGrades("", gradeList);
        Assert.assertEquals(1, subjectWithGrades.getItemCount());

        SubjectWithGrades subjectWithGrades1 = new SubjectWithGrades("", gradeListEmpty);
        Assert.assertEquals(0, subjectWithGrades1.getItemCount());
    }

    @Test
    public void titleTest() {
        SubjectWithGrades subjectWithGrades = new SubjectWithGrades("TEST", gradeListEmpty);
        Assert.assertEquals("TEST", subjectWithGrades.getTitle());
    }

    @Test
    public void itemTest() {
        SubjectWithGrades subjectWithGrades = new SubjectWithGrades("", gradeList);
        Assert.assertEquals(gradeList, subjectWithGrades.getItems());
    }
}
