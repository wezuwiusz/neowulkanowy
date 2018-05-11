package io.github.wulkanowy.api.grades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class GradesListTest extends StudentAndParentTestCase {

    private GradesList filled;

    @Before
    public void setUp() throws Exception {
        filled = new GradesList(getSnp("OcenyWszystkie-filled.html"));
    }

    @Test
    public void getAllTest() throws Exception {
        Assert.assertEquals(7, filled.getAll().size()); // 2 items are skipped
    }

    @Test
    public void getSubjectTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("Zajęcia z wychowawcą", list.get(0).getSubject());
        Assert.assertEquals("Język angielski", list.get(3).getSubject());
        Assert.assertEquals("Wychowanie fizyczne", list.get(4).getSubject());
        Assert.assertEquals("Język polski", list.get(5).getSubject());
    }

    @Test
    public void getValueTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("5", list.get(0).getValue());
        Assert.assertEquals("5", list.get(3).getValue());
        Assert.assertEquals("1", list.get(4).getValue());
        Assert.assertEquals("1", list.get(5).getValue());
    }

    @Test
    public void getColorTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("000000", list.get(0).getColor());
        Assert.assertEquals("1289F7", list.get(3).getColor());
        Assert.assertEquals("6ECD07", list.get(4).getColor());
        Assert.assertEquals("6ECD07", list.get(5).getColor());
    }

    @Test
    public void getSymbolTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("A1", list.get(0).getSymbol());
        Assert.assertEquals("BW3", list.get(3).getSymbol());
        Assert.assertEquals("STR", list.get(4).getSymbol());
        Assert.assertEquals("K", list.get(5).getSymbol());
        Assert.assertEquals("+Odp", list.get(6).getSymbol());
    }

    @Test
    public void getDescriptionTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("Dzień Kobiet w naszej klasie", list.get(0).getDescription());
        Assert.assertEquals("Writing", list.get(3).getDescription());
        Assert.assertEquals("", list.get(4).getDescription());
        Assert.assertEquals("Kordian", list.get(5).getDescription());
        Assert.assertEquals("Kordian", list.get(6).getDescription());
    }

    @Test
    public void getWeightTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("1,00", list.get(0).getWeight());
        Assert.assertEquals("3,00", list.get(3).getWeight());
        Assert.assertEquals("8,00", list.get(4).getWeight());
        Assert.assertEquals("5,00", list.get(5).getWeight());
    }

    @Test
    public void getDateTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("2017-03-21", list.get(0).getDate());
        Assert.assertEquals("2017-06-02", list.get(3).getDate());
        Assert.assertEquals("2017-04-02", list.get(4).getDate());
        Assert.assertEquals("2017-02-06", list.get(5).getDate());
    }

    @Test
    public void getTeacherTest() throws Exception {
        List<Grade> list = filled.getAll();

        Assert.assertEquals("Patryk Maciejewski", list.get(0).getTeacher());
        Assert.assertEquals("Oliwia Woźniak", list.get(3).getTeacher());
        Assert.assertEquals("Klaudia Dziedzic", list.get(4).getTeacher());
        Assert.assertEquals("Amelia Stępień", list.get(5).getTeacher());
    }
}
