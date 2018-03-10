package io.github.wulkanowy.api.attendance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class AttendanceTableTest extends StudentAndParentTestCase {

    private AttendanceTable excellent;

    private AttendanceTable full;

    @Before
    public void setUp() throws Exception {
        excellent = new AttendanceTable(getSnp("Frekwencja-excellent.html"));
        full = new AttendanceTable(getSnp("Frekwencja-full.html"));
    }

    @Test
    public void getWeekStartByDate() throws Exception {
        Assert.assertEquals("2015-08-31", excellent.getWeekTable().getStartDayDate());
        Assert.assertEquals("2016-09-05", full.getWeekTable().getStartDayDate());
    }

    @Test
    public void getWeekDaysNumber() throws Exception {
        Assert.assertEquals(5, excellent.getWeekTable().getDays().size());
        Assert.assertEquals(5, full.getWeekTable().getDays().size());
    }

    @Test
    public void getDayLessonsNumber() throws Exception {
        Assert.assertEquals(14, excellent.getWeekTable().getDay(0).getLessons().size());
        Assert.assertEquals(14, full.getWeekTable().getDay(0).getLessons().size());
    }

    @Test
    public void getDayDate() throws Exception {
        Assert.assertEquals("2015-08-31", excellent.getWeekTable().getDay(0).getDate());
        Assert.assertEquals("2015-09-02", excellent.getWeekTable().getDay(2).getDate());
        Assert.assertEquals("2015-09-04", excellent.getWeekTable().getDay(4).getDate());

        Assert.assertEquals("2016-09-05", full.getWeekTable().getDay(0).getDate());
        Assert.assertEquals("2016-09-07", full.getWeekTable().getDay(2).getDate());
        Assert.assertEquals("2016-09-09", full.getWeekTable().getDay(4).getDate());
    }

    @Test
    public void getLessonSubject() throws Exception {
        Assert.assertEquals("",
                excellent.getWeekTable().getDay(0).getLesson(7).getSubject());
        Assert.assertEquals("Uroczyste rozpoczęcie roku szkolnego 2015/2016",
                excellent.getWeekTable().getDay(1).getLesson(1).getSubject());
        Assert.assertEquals("Geografia",
                excellent.getWeekTable().getDay(3).getLesson(4).getSubject());

        Assert.assertEquals("Naprawa komputera",
                full.getWeekTable().getDay(1).getLesson(8).getSubject());
        Assert.assertEquals("Religia",
                full.getWeekTable().getDay(3).getLesson(1).getSubject());
        Assert.assertEquals("Metodologia programowania",
                full.getWeekTable().getDay(4).getLesson(5).getSubject());
    }

    @Test
    public void getLessonIsNotExist() throws Exception {
        Assert.assertTrue(excellent.getWeekTable().getDay(0).getLesson(5).isNotExist());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(1).isNotExist());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(12).isNotExist());

        Assert.assertFalse(full.getWeekTable().getDay(1).getLesson(12).isAbsenceUnexcused());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isAbsenceUnexcused());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(5).isAbsenceUnexcused());
    }

    @Test
    public void getLessonIsEmpty() throws Exception {
        Assert.assertTrue(excellent.getWeekTable().getDay(0).getLesson(0).isEmpty());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(6).isEmpty());
        Assert.assertTrue(excellent.getWeekTable().getDay(4).getLesson(12).isEmpty());

        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(9).isEmpty());
        Assert.assertFalse(full.getWeekTable().getDay(2).getLesson(5).isEmpty());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(2).isEmpty());
    }

    @Test
    public void getLessonIsPresence() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(7).isPresence());
        Assert.assertTrue(excellent.getWeekTable().getDay(1).getLesson(1).isPresence());
        Assert.assertTrue(excellent.getWeekTable().getDay(3).getLesson(7).isPresence());

        Assert.assertTrue(full.getWeekTable().getDay(0).getLesson(1).isPresence());
        Assert.assertTrue(full.getWeekTable().getDay(2).getLesson(6).isPresence());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(7).isPresence());
    }

    @Test
    public void getLessonIsAbsenceUnexcused() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(7).isAbsenceUnexcused());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(0).isAbsenceUnexcused());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(4).isAbsenceUnexcused());

        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(8).isAbsenceUnexcused());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isAbsenceUnexcused());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(8).isAbsenceUnexcused());
    }

    @Test
    public void getLessonIsAbsenceExcused() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(7).isAbsenceExcused());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(0).isAbsenceExcused());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(4).isAbsenceExcused());

        Assert.assertFalse(full.getWeekTable().getDay(2).getLesson(5).isAbsenceExcused());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isAbsenceExcused());
        Assert.assertTrue(full.getWeekTable().getDay(4).getLesson(3).isAbsenceExcused());
    }

    @Test
    public void getLessonIsAbsenceForSchoolReasons() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(4).isAbsenceForSchoolReasons());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(8).isAbsenceForSchoolReasons());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(12).isAbsenceForSchoolReasons());

        Assert.assertTrue(full.getWeekTable().getDay(2).getLesson(5).isAbsenceForSchoolReasons());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isAbsenceForSchoolReasons());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(8).isAbsenceForSchoolReasons());
    }

    @Test
    public void getLessonIsUnexcusedLateness() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(4).isUnexcusedLateness());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(8).isUnexcusedLateness());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(12).isUnexcusedLateness());

        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(6).isUnexcusedLateness());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isUnexcusedLateness());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(8).isUnexcusedLateness());
    }

    @Test
    public void getLessonIsExcusedLateness() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(4).isExcusedLateness());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(8).isExcusedLateness());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(12).isExcusedLateness());

        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(7).isExcusedLateness());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isExcusedLateness());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(8).isExcusedLateness());
    }

    @Test
    public void getLessonIsExemption() throws Exception {
        Assert.assertFalse(excellent.getWeekTable().getDay(0).getLesson(4).isExemption());
        Assert.assertFalse(excellent.getWeekTable().getDay(2).getLesson(8).isExemption());
        Assert.assertFalse(excellent.getWeekTable().getDay(4).getLesson(12).isExemption());

        Assert.assertFalse(full.getWeekTable().getDay(2).getLesson(5).isExemption());
        Assert.assertFalse(full.getWeekTable().getDay(3).getLesson(1).isExemption());
        Assert.assertTrue(full.getWeekTable().getDay(4).getLesson(8).isExemption());
    }
}
