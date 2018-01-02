package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.api.timetable.Day;
import io.github.wulkanowy.api.timetable.Lesson;

public class DataObjectConverterTest {

    @Test
    public void subjectConversionTest() {
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject().setName("Matematyka"));
        List<io.github.wulkanowy.db.dao.entities.Subject> subjectEntitiesList =
                DataObjectConverter.subjectsToSubjectEntities(subjectList);

        Assert.assertEquals("Matematyka", subjectEntitiesList.get(0).getName());
    }

    @Test
    public void subjectConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                DataObjectConverter.subjectsToSubjectEntities(new ArrayList<Subject>()));
    }

    @Test
    public void gradesConversionTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setDescription("Lorem ipsum"));
        List<io.github.wulkanowy.db.dao.entities.Grade> gradeEntitiesList =
                DataObjectConverter.gradesToGradeEntities(gradeList);

        Assert.assertEquals("Lorem ipsum", gradeEntitiesList.get(0).getDescription());
    }

    @Test
    public void gradeConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                DataObjectConverter.gradesToGradeEntities(new ArrayList<Grade>()));
    }

    @Test
    public void dayConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                DataObjectConverter.daysToDaysEntities(new ArrayList<Day>()));
    }

    @Test
    public void dayConversionTest() {
        List<Day> dayList = new ArrayList<>();
        dayList.add(new Day().setDate("20.12.2012"));
        List<io.github.wulkanowy.db.dao.entities.Day> dayEntityList =
                DataObjectConverter.daysToDaysEntities(dayList);

        Assert.assertEquals("20.12.2012", dayEntityList.get(0).getDate());
    }

    @Test
    public void lessonConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                DataObjectConverter.lessonsToLessonsEntities(new ArrayList<Lesson>()));
    }

    @Test
    public void lessonConversionTest() {
        List<Lesson> lessonList = new ArrayList<>();
        lessonList.add(new Lesson().setRoom("20"));
        List<io.github.wulkanowy.db.dao.entities.Lesson> lessonEntityList =
                DataObjectConverter.lessonsToLessonsEntities(lessonList);

        Assert.assertEquals("20", lessonEntityList.get(0).getRoom());
    }
}
