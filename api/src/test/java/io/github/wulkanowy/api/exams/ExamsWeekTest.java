package io.github.wulkanowy.api.exams;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class ExamsWeekTest extends StudentAndParentTestCase {

    private ExamsWeek onePerDay;

    private ExamsWeek empty;

    @Before
    public void getCurrent() throws Exception {
        onePerDay = new ExamsWeek(getSnp("Sprawdziany-one-per-day.html"));
        empty = new ExamsWeek(getSnp("Sprawdziany-empty.html"));
    }

    @Test
    public void getWeekTest() throws Exception {
        Assert.assertEquals("2017-10-23", onePerDay.getCurrent().getStartDayDate());
        Assert.assertEquals("2018-04-30", empty.getCurrent().getStartDayDate());
    }

    @Test
    public void getDaysListTest() throws Exception {
        Assert.assertEquals(5, onePerDay.getCurrent().getDays().size());
        Assert.assertEquals(7, onePerDay.getWeek("", false).getDays().size());
        Assert.assertEquals(0, empty.getCurrent().getDays().size());
    }

    @Test
    public void getExamsListTest() throws Exception {
        List<ExamDay> notEmpty = onePerDay.getCurrent().getDays();
        Assert.assertEquals(1, notEmpty.get(0).getExamList().size());
        Assert.assertEquals(1, notEmpty.get(1).getExamList().size());
        Assert.assertEquals(1, notEmpty.get(4).getExamList().size());

        List<ExamDay> emptyToo = onePerDay.getWeek("", false).getDays();
        Assert.assertEquals(1, emptyToo.get(0).getExamList().size());
        Assert.assertEquals(1, emptyToo.get(1).getExamList().size());
        Assert.assertEquals(1, emptyToo.get(4).getExamList().size());
    }

    @Test
    public void getDayDateTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("2017-10-23", dayList.get(0).getDate());
        Assert.assertEquals("2017-10-24", dayList.get(1).getDate());
        Assert.assertEquals("2017-10-27", dayList.get(4).getDate());
    }

    @Test
    public void getDayNameTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Poniedziałek", dayList.get(0).getDayName());
        Assert.assertEquals("Wtorek", dayList.get(1).getDayName());
        Assert.assertEquals("Piątek", dayList.get(4).getDayName());
    }

    @Test
    public void getExamSubjectAndGroupTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Sieci komputerowe 3Ti|zaw2", dayList.get(0).getExamList().get(0).getSubjectAndGroup());
        Assert.assertEquals("Język angielski 3Ti|J1", dayList.get(1).getExamList().get(0).getSubjectAndGroup());
        Assert.assertEquals("Metodologia programowania 3Ti|zaw2", dayList.get(4).getExamList().get(0).getSubjectAndGroup());
    }

    @Test
    public void getExamTypeTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Sprawdzian", dayList.get(0).getExamList().get(0).getType());
        Assert.assertEquals("Sprawdzian", dayList.get(1).getExamList().get(0).getType());
        Assert.assertEquals("Sprawdzian", dayList.get(4).getExamList().get(0).getType());
    }

    @Test
    public void getExamDescriptionTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Łącza danych", dayList.get(0).getExamList().get(0).getDescription());
        Assert.assertEquals("Czasy teraźniejsze", dayList.get(1).getExamList().get(0).getDescription());
        Assert.assertEquals("", dayList.get(4).getExamList().get(0).getDescription());
    }

    @Test
    public void getExamTeacherTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("Adam Wiśniewski [AW]", dayList.get(0).getExamList().get(0).getTeacher());
        Assert.assertEquals("Natalia Nowak [NN]", dayList.get(1).getExamList().get(0).getTeacher());
        Assert.assertEquals("Małgorzata Nowacka [MN]", dayList.get(4).getExamList().get(0).getTeacher());
    }

    @Test
    public void getExamEntryDateTest() throws Exception {
        List<ExamDay> dayList = onePerDay.getCurrent().getDays();

        Assert.assertEquals("2017-10-16", dayList.get(0).getExamList().get(0).getEntryDate());
        Assert.assertEquals("2017-10-17", dayList.get(1).getExamList().get(0).getEntryDate());
        Assert.assertEquals("2017-10-16", dayList.get(4).getExamList().get(0).getEntryDate());
    }
}
