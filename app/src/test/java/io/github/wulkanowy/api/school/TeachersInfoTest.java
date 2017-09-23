package io.github.wulkanowy.api.school;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class TeachersInfoTest extends StudentAndParentTestCase {

    private TeachersInfo teachersInfo;

    @Before
    public void setUp() throws Exception {
        teachersInfo = new TeachersInfo(getSnp("Szkola.html"));
    }

    @Test
    public void getClassNameTest() throws Exception {
        Assert.assertEquals("1a", teachersInfo.getTeachersData().getClassName());
    }

    @Test
    public void getClassTeacherTest() throws Exception {
        Assert.assertArrayEquals(new String[]{
                "Karolina Kowalska [AN]",
                "Antoni Sobczyk [AS]"
        }, teachersInfo.getTeachersData().getClassTeacher());
    }

    @Test
    public void getTeachersDataSubjectsNameTest() throws Exception {
        List<Subject> subjects = teachersInfo.getTeachersData().getSubjects();

        Assert.assertEquals("Biologia", subjects.get(0).getName());
        Assert.assertEquals("Język angielski", subjects.get(6).getName());
    }

    @Test
    public void getTeachersDataSubjectsTeachersTest() throws Exception {
        List<Subject> subjects = teachersInfo.getTeachersData().getSubjects();

        Assert.assertArrayEquals(new String[]{"Karolina Kowalska [AN]"},
                subjects.get(0).getTeachers());
        Assert.assertEquals("Karolina Kowalska [AN]",
                subjects.get(0).getTeachers()[0]);

        Assert.assertArrayEquals(new String[]{
                "Karolina Kowalska [AN]",
                "Mateusz Kowal [MK]",
                "Amelia Mazur [AM]"
        }, subjects.get(6).getTeachers());
    }
}
