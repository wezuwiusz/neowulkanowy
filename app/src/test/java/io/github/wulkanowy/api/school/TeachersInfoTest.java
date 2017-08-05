package io.github.wulkanowy.api.school;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TeachersInfoTest extends SchoolTest {

    private TeachersInfo teachersInfo;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        teachersInfo = new TeachersInfo(school);
    }

    @Test
    public void getTeachersDataTest() throws Exception {
        TeachersData teachersData = teachersInfo.getTeachersData();

        Assert.assertEquals("1a", teachersData.getClassName());
        Assert.assertArrayEquals(new String[]{
                "Karolina Kowalska [AN]",
                "Antoni Sobczyk [AS]"
        }, teachersData.getClassTeacher());

        List<Subject> subjects = teachersData.getSubjects();

        Assert.assertEquals("Biologia", subjects.get(0).getName());
        Assert.assertArrayEquals(new String[]{"Karolina Kowalska [AN]"},
                subjects.get(0).getTeachers());
        Assert.assertEquals("Karolina Kowalska [AN]",
                subjects.get(0).getTeachers()[0]);

        Assert.assertEquals("Język angielski", subjects.get(6).getName());
        Assert.assertArrayEquals(new String[]{
                "Karolina Kowalska [AN]",
                "Mateusz Kowal [MK]",
                "Amelia Mazur [AM]"
        }, subjects.get(6).getTeachers());
    }
}
