package io.github.wulkanowy.api.timetable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class TimetableTest extends StudentAndParentTestCase {

    private Timetable std;

    private Timetable full;

    private Timetable holidays;

    @Before
    public void setUp() throws Exception {
        std = new Timetable(getSnp("PlanLekcji-std.html"));
        full = new Timetable(getSnp("PlanLekcji-full.html"));
        holidays = new Timetable(getSnp("PlanLekcji-holidays.html"));
    }

    // Week

    @Test
    public void getWeekTableTest() throws Exception {
        Assert.assertEquals(5, std.getWeekTable().getDays().size());
        Assert.assertEquals(5, full.getWeekTable().getDays().size());
        Assert.assertEquals(5, holidays.getWeekTable().getDays().size());
    }

    @Test
    public void getStartDayDateTest() throws Exception {
        Assert.assertEquals("19.06.2017", std.getWeekTable().getStartDayDate());
        Assert.assertEquals("19.06.2017", full.getWeekTable().getStartDayDate());
        Assert.assertEquals("31.07.2017", holidays.getWeekTable().getStartDayDate());
    }

    // Day

    @Test
    public void getDayDateTest() throws Exception {
        Assert.assertEquals("19.06.2017", std.getWeekTable().getDay(0).getDate());
        Assert.assertEquals("23.06.2017", std.getWeekTable().getDay(4).getDate());
        Assert.assertEquals("20.06.2017", full.getWeekTable().getDay(1).getDate());
        Assert.assertEquals("22.06.2017", full.getWeekTable().getDay(3).getDate());
        Assert.assertEquals("02.08.2017", holidays.getWeekTable().getDay(2).getDate());
    }

    @Test
    public void getDayIsFreeTest() throws Exception {
        Assert.assertFalse(std.getWeekTable().getDay(0).isFreeDay());
        Assert.assertFalse(full.getWeekTable().getDay(2).isFreeDay());
        Assert.assertTrue(holidays.getWeekTable().getDay(4).isFreeDay());
    }

    @Test
    public void getDayFreeDayName() throws Exception {
        Assert.assertNotEquals("Wakacje", std.getWeekTable().getDay(0).getFreeDayName());
        Assert.assertNotEquals("Ferie letnie", full.getWeekTable().getDay(1).getFreeDayName());
        Assert.assertNotEquals("Wakacje", holidays.getWeekTable().getDay(2).getFreeDayName());
        Assert.assertEquals("Ferie letnie", holidays.getWeekTable().getDay(4).getFreeDayName());
    }

    @Test
    public void getDayLessonsTest() throws Exception {
        Assert.assertEquals(8, std.getWeekTable().getDay(0).getLessons().size());
        Assert.assertEquals(14, full.getWeekTable().getDay(2).getLessons().size());
        Assert.assertEquals(14, holidays.getWeekTable().getDay(4).getLessons().size());
    }

    // Lesson

    @Test
    public void getLessonSubjectTest() throws Exception {
        Assert.assertEquals("Historia", std.getWeekTable().getDay(0).getLesson(1).getSubject());
        Assert.assertEquals("Zajęcia techniczne", std.getWeekTable().getDay(2).getLesson(4).getSubject());
        Assert.assertEquals("Język angielski", full.getWeekTable().getDay(0).getLesson(1).getSubject());
        Assert.assertEquals("Uroczyste zakończenie roku szkolnego", full.getWeekTable().getDay(4).getLesson(0).getSubject());
        Assert.assertEquals("", holidays.getWeekTable().getDay(3).getLesson(3).getSubject());
    }

    @Test
    public void getLessonTeacherTest() throws Exception {
        Assert.assertEquals("Bogatka Katarzyna", std.getWeekTable().getDay(0).getLesson(1).getTeacher());
        Assert.assertEquals("Chlebowski Stanisław", std.getWeekTable().getDay(2).getLesson(4).getTeacher());
        Assert.assertEquals("Kobczyk Iwona", full.getWeekTable().getDay(0).getLesson(1).getTeacher());
        Assert.assertEquals("Bączek Grzegorz", full.getWeekTable().getDay(0).getLesson(7).getTeacher());
        Assert.assertEquals("Baran Małgorzata", full.getWeekTable().getDay(4).getLesson(0).getTeacher());
        Assert.assertEquals("", holidays.getWeekTable().getDay(3).getLesson(3).getTeacher());
    }

    @Test
    public void getLessonRoomTest() throws Exception {
        Assert.assertEquals("", std.getWeekTable().getDay(3).getLesson(3).getRoom());
        Assert.assertEquals("33", full.getWeekTable().getDay(0).getLesson(7).getRoom());
        Assert.assertEquals("32", full.getWeekTable().getDay(1).getLesson(8).getRoom());
        Assert.assertEquals("37", full.getWeekTable().getDay(4).getLesson(0).getRoom());
        Assert.assertEquals("", holidays.getWeekTable().getDay(3).getLesson(3).getRoom());
    }

    @Test
    public void getLessonDescriptionTest() throws Exception {
        Assert.assertEquals("", std.getWeekTable().getDay(3).getLesson(3).getDescription());
        Assert.assertEquals("okienko dla uczniów", full.getWeekTable().getDay(0).getLesson(7).getDescription());
        Assert.assertEquals("przeniesiona z lekcji 7, 20.06.2017", full.getWeekTable().getDay(1).getLesson(2).getDescription());
        Assert.assertEquals("przeniesiona z lekcji 4, 20.06.2017", full.getWeekTable().getDay(1).getLesson(3).getDescription());
        Assert.assertEquals("", full.getWeekTable().getDay(4).getLesson(0).getDescription());
        Assert.assertEquals("", holidays.getWeekTable().getDay(3).getLesson(3).getDescription());
    }

    @Test
    public void getLessonGroupNameTest() throws Exception {
        Assert.assertEquals("CH", std.getWeekTable().getDay(0).getLesson(2).getGroupName());
        Assert.assertEquals("JNPW", std.getWeekTable().getDay(4).getLesson(0).getGroupName());
        Assert.assertEquals("", full.getWeekTable().getDay(0).getLesson(7).getGroupName());
        Assert.assertEquals("wf2", full.getWeekTable().getDay(1).getLesson(3).getGroupName());
        Assert.assertEquals("", holidays.getWeekTable().getDay(3).getLesson(3).getGroupName());
    }

    @Test
    public void getLessonStartTimeTest() throws Exception {
        Assert.assertEquals("08:00", std.getWeekTable().getDay(0).getLesson(0).getStartTime());
        Assert.assertEquals("14:10", std.getWeekTable().getDay(3).getLesson(7).getStartTime());
        Assert.assertEquals("07:10", full.getWeekTable().getDay(0).getLesson(0).getStartTime());
        Assert.assertEquals("12:20", full.getWeekTable().getDay(2).getLesson(6).getStartTime());
        Assert.assertEquals("12:20", holidays.getWeekTable().getDay(2).getLesson(6).getStartTime());
    }

    @Test
    public void getLessonEndTimeTest() throws Exception {
        Assert.assertEquals("08:45", std.getWeekTable().getDay(1).getLesson(0).getEndTime());
        Assert.assertEquals("12:15", std.getWeekTable().getDay(2).getLesson(4).getEndTime());
        Assert.assertEquals("07:55", full.getWeekTable().getDay(1).getLesson(0).getEndTime());
        Assert.assertEquals("19:00", full.getWeekTable().getDay(3).getLesson(13).getEndTime());
        Assert.assertEquals("19:00", holidays.getWeekTable().getDay(3).getLesson(13).getEndTime());
    }

    @Test
    public void getLessonIsEmptyTest() throws Exception {
        Assert.assertFalse(std.getWeekTable().getDay(1).getLesson(4).isEmpty());
        Assert.assertTrue(std.getWeekTable().getDay(3).getLesson(7).isEmpty());
        Assert.assertFalse(full.getWeekTable().getDay(1).getLesson(2).isEmpty());
        Assert.assertFalse(full.getWeekTable().getDay(0).getLesson(7).isEmpty());
        Assert.assertTrue(full.getWeekTable().getDay(2).getLesson(9).isEmpty());
        Assert.assertTrue(holidays.getWeekTable().getDay(0).getLesson(5).isEmpty());
        Assert.assertTrue(holidays.getWeekTable().getDay(4).getLesson(13).isEmpty());
    }

    @Test
    public void getLessonIsDivisionIntoGroupsTest() throws Exception {
        Assert.assertTrue(std.getWeekTable().getDay(0).getLesson(2).isDivisionIntoGroups());
        Assert.assertTrue(std.getWeekTable().getDay(4).getLesson(0).isDivisionIntoGroups());
        Assert.assertFalse(full.getWeekTable().getDay(0).getLesson(7).isDivisionIntoGroups());
        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(3).isDivisionIntoGroups());
        Assert.assertFalse(holidays.getWeekTable().getDay(3).getLesson(3).isDivisionIntoGroups());
    }

    @Test
    public void getLessonIsPlanningTest() throws Exception {
        Assert.assertFalse(std.getWeekTable().getDay(4).getLesson(4).isPlanning());
        Assert.assertFalse(full.getWeekTable().getDay(0).getLesson(1).isPlanning());
        Assert.assertFalse(full.getWeekTable().getDay(1).getLesson(3).isPlanning());
        Assert.assertTrue(full.getWeekTable().getDay(4).getLesson(0).isPlanning());
        Assert.assertFalse(holidays.getWeekTable().getDay(3).getLesson(3).isPlanning());
    }

    @Test
    public void getLessonIsRealizedTest() throws Exception {
        Assert.assertTrue(std.getWeekTable().getDay(3).getLesson(3).isRealized());
        Assert.assertTrue(full.getWeekTable().getDay(0).getLesson(1).isRealized());
        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(3).isRealized());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(0).isRealized());
        Assert.assertFalse(holidays.getWeekTable().getDay(3).getLesson(3).isRealized());
    }

    @Test
    public void getLessonIsMovedOrCanceledTest() throws Exception {
        Assert.assertFalse(std.getWeekTable().getDay(3).getLesson(3).isMovedOrCanceled());
        Assert.assertTrue(full.getWeekTable().getDay(0).getLesson(7).isMovedOrCanceled());
        Assert.assertFalse(full.getWeekTable().getDay(1).getLesson(3).isMovedOrCanceled());
        Assert.assertFalse(full.getWeekTable().getDay(4).getLesson(0).isMovedOrCanceled());
        Assert.assertFalse(holidays.getWeekTable().getDay(3).getLesson(3).isMovedOrCanceled());
    }

    @Test
    public void getLessonIsNewMovedInOrChangedTest() throws Exception {
        Assert.assertFalse(std.getWeekTable().getDay(3).getLesson(3).isNewMovedInOrChanged());
        Assert.assertFalse(full.getWeekTable().getDay(0).getLesson(1).isNewMovedInOrChanged());
        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(2).isNewMovedInOrChanged());
        Assert.assertTrue(full.getWeekTable().getDay(1).getLesson(3).isNewMovedInOrChanged());
        Assert.assertFalse(holidays.getWeekTable().getDay(3).getLesson(3).isNewMovedInOrChanged());
    }
}
