package io.github.wulkanowy.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.api.grades.Subject;

public class ConversionVulcanObjectTest extends ConversionVulcanObject {

    @Test
    public void subjectConversionTest() {
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject().setName("Matematyka"));
        List<io.github.wulkanowy.dao.entities.Subject> subjectEntitiesList =
                ConversionVulcanObject.subjectsToSubjectEntities(subjectList);

        Assert.assertEquals("Matematyka", subjectEntitiesList.get(0).getName());
    }

    @Test
    public void subjectConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                ConversionVulcanObject.subjectsToSubjectEntities(new ArrayList<Subject>()));
    }

    @Test
    public void gradesConversionTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setDescription("Lorem ipsum"));
        List<io.github.wulkanowy.dao.entities.Grade> gradeEntitiesList =
                ConversionVulcanObject.gradesToGradeEntities(gradeList);

        Assert.assertEquals("Lorem ipsum", gradeEntitiesList.get(0).getDescription());
    }

    @Test
    public void gradeConversionEmptyTest() {
        Assert.assertEquals(new ArrayList<>(),
                ConversionVulcanObject.gradesToGradeEntities(new ArrayList<Grade>()));
    }
}
