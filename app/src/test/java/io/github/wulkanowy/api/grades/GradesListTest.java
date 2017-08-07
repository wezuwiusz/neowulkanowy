package io.github.wulkanowy.api.grades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GradesListTest extends GradesTest {

    private String fixtureFileName = "OcenyWszystkie-filled.html";

    private GradesList gradesList;

    @Before
    public void setUp() throws Exception {
        super.setUp(fixtureFileName);

        gradesList = new GradesList(snp);
    }

    @Test
    public void getAllTest() throws Exception {
        List<Grade> grades = gradesList.getAll();
        Assert.assertEquals(6, grades.size()); // 2 items are skipped

        Grade grade1 = grades.get(0);
        Assert.assertEquals("Zajęcia z wychowawcą", grade1.getSubject());
        Assert.assertEquals("5", grade1.getValue());
        Assert.assertEquals("000000", grade1.getColor());
        Assert.assertEquals("A1", grade1.getSymbol());
        Assert.assertEquals("Dzień Kobiet w naszej klasie", grade1.getDescription());
        Assert.assertEquals("1,00", grade1.getWeight());
        Assert.assertEquals("21.03.2017", grade1.getDate());
        Assert.assertEquals("Patryk Maciejewski", grade1.getTeacher());
        Assert.assertEquals("7654321", grade1.getSemester());

        Grade grade2 = grades.get(3);
        Assert.assertEquals("Język angielski", grade2.getSubject());
        Assert.assertEquals("5", grade2.getValue());
        Assert.assertEquals("1289F7", grade2.getColor());
        Assert.assertEquals("BW3", grade2.getSymbol());
        Assert.assertEquals("Writing", grade2.getDescription());
        Assert.assertEquals("3,00", grade2.getWeight());
        Assert.assertEquals("02.06.2017", grade2.getDate());
        Assert.assertEquals("Oliwia Woźniak", grade2.getTeacher());
        Assert.assertEquals("7654321", grade2.getSemester());

        Grade grade3 = grades.get(4);
        Assert.assertEquals("Wychowanie fizyczne", grade3.getSubject());
        Assert.assertEquals("1", grade3.getValue());
        Assert.assertEquals("6ECD07", grade3.getColor());
        Assert.assertEquals("STR", grade3.getSymbol());
        Assert.assertEquals("", grade3.getDescription());
        Assert.assertEquals("8,00", grade3.getWeight());
        Assert.assertEquals("02.04.2017", grade3.getDate());
        Assert.assertEquals("Klaudia Dziedzic", grade3.getTeacher());
        Assert.assertEquals("7654321", grade3.getSemester());

        Grade grade4 = grades.get(5);
        Assert.assertEquals("Język polski", grade4.getSubject());
        Assert.assertEquals("1", grade4.getValue());
        Assert.assertEquals("6ECD07", grade4.getColor());
        Assert.assertEquals("K", grade4.getSymbol());
        Assert.assertEquals("Kordian", grade4.getDescription());
        Assert.assertEquals("5,00", grade4.getWeight());
        Assert.assertEquals("06.02.2017", grade4.getDate());
        Assert.assertEquals("Amelia Stępień", grade4.getTeacher());
        Assert.assertEquals("7654321", grade4.getSemester());
    }
}
