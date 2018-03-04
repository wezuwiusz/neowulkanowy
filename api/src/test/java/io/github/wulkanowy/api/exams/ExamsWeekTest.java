package io.github.wulkanowy.api.exams;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class ExamsWeekTest extends StudentAndParentTestCase {

    private ExamsWeek onePerDay;

    @Before
    public void getCurrent() throws Exception {
        onePerDay = new ExamsWeek(getSnp("Sprawdziany-one-per-day.html"));
    }

    @Test
    public void getWeekTest() throws Exception {
        Assert.assertEquals("23.10.2017", onePerDay.getCurrent().getStartDayDate());
    }

    @Test
    public void getDaysListTest() throws Exception {
        Assert.assertEquals(3, onePerDay.getCurrent().getDays().size());
        Assert.assertEquals(7, onePerDay.getWeek("", false).getDays().size());
    }

    @Test
    public void getExamsListTest() throws Exception {
        List<ExamDay> notEmpty = onePerDay.getCurrent().getDays();
        Assert.assertEquals(1, notEmpty.get(0).getExamList().size());
        Assert.assertEquals(1, notEmpty.get(1).getExamList().size());
        Assert.assertEquals(1, notEmpty.get(2).getExamList().size());

        List<ExamDay> emptyToo = onePerDay.getWeek("", false).getDays();
        Assert.assertEquals(1, emptyToo.get(0).getExamList().size());
        Assert.assertEquals(1, emptyToo.get(1).getExamList().size());
        Assert.assertEquals(1, emptyToo.get(4).getExamList().size());
    }

    @Test
    public void getDayDateTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("23.10.2017", dayList.get(0).getDate());
        Assert.assertEquals("24.10.2017", dayList.get(1).getDate());
        Assert.assertEquals("27.10.2017", dayList.get(2).getDate());
    }

    @Test
    public void getExamSubjectAndGroupTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Sieci komputerowe 3Ti|zaw2", dayList.get(0).getExamList().get(0).getSubjectAndGroup());
        Assert.assertEquals("Język angielski 3Ti|J1", dayList.get(1).getExamList().get(0).getSubjectAndGroup());
        Assert.assertEquals("Metodologia programowania 3Ti|zaw2", dayList.get(2).getExamList().get(0).getSubjectAndGroup());
    }

    @Test
    public void getExamTypeTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Sprawdzian", dayList.get(0).getExamList().get(0).getType());
        Assert.assertEquals("Sprawdzian", dayList.get(1).getExamList().get(0).getType());
        Assert.assertEquals("Sprawdzian", dayList.get(2).getExamList().get(0).getType());
    }

    @Test
    public void getExamDescriptionTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Łącza danych", dayList.get(0).getExamList().get(0).getDescription());
        Assert.assertEquals("Czasy teraźniejsze", dayList.get(1).getExamList().get(0).getDescription());
        Assert.assertEquals("", dayList.get(2).getExamList().get(0).getDescription());
    }

    @Test
    public void getExamTeacherTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Adam Wiśniewski [AW]", dayList.get(0).getExamList().get(0).getTeacher());
        Assert.assertEquals("Natalia Nowak [NN]", dayList.get(1).getExamList().get(0).getTeacher());
        Assert.assertEquals("Małgorzata Nowacka [MN]", dayList.get(2).getExamList().get(0).getTeacher());
    }

    @Test
    public void getExamEntryDateTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("16.10.2017", dayList.get(0).getExamList().get(0).getEntryDate());
        Assert.assertEquals("17.10.2017", dayList.get(1).getExamList().get(0).getEntryDate());
        Assert.assertEquals("16.10.2017", dayList.get(2).getExamList().get(0).getEntryDate());
    }
}
