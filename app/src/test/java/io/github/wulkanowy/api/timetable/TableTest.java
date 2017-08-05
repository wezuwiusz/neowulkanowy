package io.github.wulkanowy.api.timetable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.github.wulkanowy.api.FixtureHelper;

public class TableTest {

    private String fixtureStdFileName = "PlanLekcji-std.html";
    private String fixtureHolidaysFileName = "PlanLekcji-holidays.html";
    private String fixtureFullFileName = "PlanLekcji-full.html";

    private Table getSetUpTable(String tick, String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document tablePageDocument = Jsoup.parse(input);

        Timetable timetable = Mockito.mock(Timetable.class);
        Mockito.when(timetable.getTablePageDocument(tick)).thenReturn(tablePageDocument);

        return new Table(timetable);
    }

    @Test
    public void getWeekTableStandardTest() throws Exception {
        Table table = getSetUpTable("", fixtureStdFileName);
        Week week = table.getWeekTable();

        Assert.assertEquals(5, week.getDays().size());
        Assert.assertEquals("19.06.2017", week.getStartDayDate());

        Assert.assertEquals("19.06.2017", week.getDay(0).getDate());
        Assert.assertEquals("23.06.2017", week.getDay(4).getDate());

        Assert.assertFalse(week.getDay(4).isFreeDay());
    }

    @Test
    public void getWeekTableStandardLessonStartEndEndTest() throws Exception {
        Table tableStd = getSetUpTable("", fixtureStdFileName);
        Week stdWeek = tableStd.getWeekTable();

        Assert.assertEquals("08:00", stdWeek.getDay(0).getLesson(0).getStartTime());
        Assert.assertEquals("08:45", stdWeek.getDay(1).getLesson(0).getEndTime());
        Assert.assertEquals("12:15", stdWeek.getDay(2).getLesson(4).getEndTime());
        Assert.assertEquals("14:10", stdWeek.getDay(3).getLesson(7).getStartTime());

        Table tableFull = getSetUpTable("", fixtureFullFileName);
        Week fullWeek = tableFull.getWeekTable();

        Assert.assertEquals("07:10", fullWeek.getDay(0).getLesson(0).getStartTime());
        Assert.assertEquals("07:55", fullWeek.getDay(1).getLesson(0).getEndTime());
        Assert.assertEquals("12:20", fullWeek.getDay(2).getLesson(6).getStartTime());
        Assert.assertEquals("19:00", fullWeek.getDay(3).getLesson(13).getEndTime());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWeekTableStandardOutOfBoundsIndex() throws Exception {
        Table table = getSetUpTable("", fixtureStdFileName);
        Week week = table.getWeekTable();

        week.getDay(5);
    }

    @Test
    public void getWeekTableHolidaysTest() throws Exception {
        Table table = getSetUpTable("", fixtureHolidaysFileName);
        Week week = table.getWeekTable();

        Assert.assertTrue(week.getDay(1).isFreeDay());

        Assert.assertNotEquals("Wakacje", week.getDay(2).getFreeDayName());

        Assert.assertEquals("Ferie letnie", week.getDay(3).getFreeDayName());
        Assert.assertEquals("31.07.2017", week.getStartDayDate());
        Assert.assertEquals(5, week.getDays().size());
        Assert.assertEquals(14, week.getDay(4).getLessons().size());
    }

    @Test
    public void getWeekTableHolidaysWithEmptyLessonsTest() throws Exception {
        Table table = getSetUpTable("", fixtureHolidaysFileName);
        Week week = table.getWeekTable();

        Assert.assertEquals(5, week.getDays().size());

        Assert.assertTrue(week.getDay(0).getLesson(5).isEmpty());
        Assert.assertTrue(week.getDay(2).getLesson(13).isEmpty());
        Assert.assertTrue(week.getDay(3).getLesson(0).isEmpty());
        Assert.assertTrue(week.getDay(4).getLesson(13).isEmpty());
    }

    @Test
    public void getWeekTableFullTest() throws Exception {
        Table table = getSetUpTable("", fixtureFullFileName);
        Week week = table.getWeekTable();

        Assert.assertFalse(week.getDay(1).getLesson(2).isEmpty());
    }

    @Test
    public void getWeekTableFullLessonsGroupsDivisionTest() throws Exception {
        Table table = getSetUpTable("", fixtureFullFileName);
        Week week = table.getWeekTable();

        // class="", span*4
        Lesson lesson1 = week.getDay(0).getLesson(1);
        Assert.assertTrue(lesson1.isDivisionIntoGroups());
        Assert.assertEquals("J1", lesson1.getGroupName());

        // class="", span*3
        Lesson lesson2 = week.getDay(0).getLesson(7);
        Assert.assertFalse(lesson2.isDivisionIntoGroups());
        Assert.assertEquals("", lesson2.getGroupName());

        // div*3 (2), class="x-treelabel-zas", span*4
        Lesson lesson3 = week.getDay(1).getLesson(2);
        Assert.assertFalse(lesson3.isDivisionIntoGroups());
        Assert.assertEquals("", lesson3.getGroupName());

        // div*3 (2), class="x-treelabel-zas", span*5
        Lesson lesson4 = week.getDay(1).getLesson(3);
        Assert.assertTrue(lesson4.isDivisionIntoGroups());
        Assert.assertEquals("wf2", lesson4.getGroupName());

        // class="x-treelabel-ppl", span*3
        Lesson lesson5 = week.getDay(4).getLesson(0);
        Assert.assertFalse(lesson5.isDivisionIntoGroups());
        Assert.assertEquals("", lesson5.getGroupName());
    }

    @Test
    public void getWeekTableFullLessonsTypesTest() throws Exception {
        Table table = getSetUpTable("", fixtureFullFileName);
        Week week = table.getWeekTable();

        // class="", span*4
        Lesson lesson1 = week.getDay(0).getLesson(1);
        Assert.assertFalse(lesson1.isPlanning());
        Assert.assertTrue(lesson1.isRealized());
        Assert.assertFalse(lesson1.isMovedOrCanceled());
        Assert.assertFalse(lesson1.isNewMovedInOrChanged());

        // class="", span*3
        Lesson lesson2 = week.getDay(0).getLesson(7);
        Assert.assertFalse(lesson2.isPlanning());
        Assert.assertTrue(lesson2.isRealized());
        Assert.assertTrue(lesson2.isMovedOrCanceled());
        Assert.assertFalse(lesson2.isNewMovedInOrChanged());

        // div*3 (2), class="x-treelabel-zas", span*4
        Lesson lesson3 = week.getDay(1).getLesson(2);
        Assert.assertFalse(lesson3.isPlanning());
        Assert.assertTrue(lesson3.isRealized());
        Assert.assertFalse(lesson3.isMovedOrCanceled());
        Assert.assertTrue(lesson3.isNewMovedInOrChanged());

        // div*3 (2), class="x-treelabel-zas", span*5
        Lesson lesson4 = week.getDay(1).getLesson(3);
        Assert.assertFalse(lesson4.isPlanning());
        Assert.assertTrue(lesson4.isRealized());
        Assert.assertFalse(lesson4.isMovedOrCanceled());
        Assert.assertTrue(lesson4.isNewMovedInOrChanged());

        // class="x-treelabel-ppl", span*3
        Lesson lesson5 = week.getDay(4).getLesson(0);
        Assert.assertTrue(lesson5.isPlanning());
        Assert.assertFalse(lesson5.isRealized());
        Assert.assertFalse(lesson5.isMovedOrCanceled());
        Assert.assertFalse(lesson5.isNewMovedInOrChanged());
    }

    @Test
    public void getWeekTableFullLessonsBasicInfoTest() throws Exception {
        Table table = getSetUpTable("", fixtureFullFileName);
        Week week = table.getWeekTable();

        // class="", span*4
        Lesson lesson1 = week.getDay(0).getLesson(1);
        Assert.assertEquals("Język angielski", lesson1.getSubject());
        Assert.assertEquals("Kobczyk Iwona", lesson1.getTeacher());
        Assert.assertEquals("", lesson1.getRoom());
        Assert.assertEquals("", lesson1.getDescription());

        // class="", span*3
        Lesson lesson2 = week.getDay(0).getLesson(7);
        Assert.assertEquals("Fizyka", lesson2.getSubject());
        Assert.assertEquals("Bączek Grzegorz", lesson2.getTeacher());
        Assert.assertEquals("33", lesson2.getRoom());
        Assert.assertEquals("okienko dla uczniów", lesson2.getDescription());

        // div*3 (2), class="x-treelabel-zas", span*4
        Lesson lesson3 = week.getDay(1).getLesson(2);
        Assert.assertEquals("Język polski", lesson3.getSubject());
        Assert.assertEquals("Bocian Natalia", lesson3.getTeacher());
        Assert.assertEquals("", lesson3.getRoom());
        Assert.assertEquals("przeniesiona z lekcji 7, 20.06.2017", lesson3.getDescription());

        // div*3 (2), class="x-treelabel-zas", span*5
        Lesson lesson4 = week.getDay(1).getLesson(3);
        Assert.assertEquals("Wychowanie fizyczne", lesson4.getSubject());
        Assert.assertEquals("Nowicka Irena", lesson4.getTeacher());
        Assert.assertEquals("", lesson4.getRoom());
        Assert.assertEquals("przeniesiona z lekcji 4, 20.06.2017", lesson4.getDescription());

        // class="x-treelabel-ppl", span*3
        Lesson lesson5 = week.getDay(4).getLesson(0);
        Assert.assertEquals("Uroczyste zakończenie roku szkolnego", lesson5.getSubject());
        Assert.assertEquals("Baran Małgorzata", lesson5.getTeacher());
        Assert.assertEquals("37", lesson5.getRoom());
        Assert.assertEquals("", lesson5.getDescription());
    }
}
