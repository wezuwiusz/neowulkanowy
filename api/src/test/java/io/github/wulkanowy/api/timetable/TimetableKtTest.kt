package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.*
import org.junit.Test

class TimetableKtTest : StudentAndParentTestCase() {

    private val std by lazy { TimetableKt(getSnp("PlanLekcji-std.html")) }

    private val full by lazy { TimetableKt(getSnp("PlanLekcji-full.html")) }

    private val holidays by lazy { TimetableKt(getSnp("PlanLekcji-holidays.html")) }

    @Test fun getTimetableTest() {
        assertEquals(32, std.getTimetable().size)
        assertEquals(43, full.getTimetable().size)
        assertEquals(0, holidays.getTimetable().size)
    }

    @Test fun getStartDayDateTest() {
        assertEquals("2017-06-19", std.getTimetable()[0].date)
        assertEquals("2017-06-20", std.getTimetable()[8].date)
        assertEquals("2017-06-21", std.getTimetable()[16].date)

        assertEquals("2017-06-19", full.getTimetable()[0].date)
        assertEquals("2017-06-20", full.getTimetable()[10].date)
        assertEquals("2017-06-22", full.getTimetable()[30].date)
    }

    @Test fun getDayIsFreeTest() {
        assertFalse(std.getTimetable().first().freeDayName.isNotEmpty())
        assertFalse(full.getTimetable().last().freeDayName.isNotEmpty())
    }

    @Test fun getDayFreeDayName() {
        assertNotEquals("Wakacje", std.getTimetable()[0].freeDayName)
        assertNotEquals("Ferie letnie", full.getTimetable()[15].freeDayName)
    }

    @Test fun getLessonNumberTest() {
        assertEquals(2, std.getTimetable()[1].number)
        assertEquals(5, std.getTimetable()[10].number)

        assertEquals(0, full.getTimetable()[0].number)
        assertEquals(9, full.getTimetable()[19].number)
    }

    @Test fun getLessonSubjectTest() {
        assertEquals("Historia", std.getTimetable()[1].subject)
        assertEquals("Zajęcia techniczne", std.getTimetable()[17].subject)
        assertEquals("Wychowanie fizyczne", std.getTimetable()[7].subject)

        assertEquals("Fizyka", full.getTimetable()[0].subject)
        assertEquals("Język angielski", full.getTimetable()[1].subject)
        assertEquals("Wychowanie fizyczne", full.getTimetable()[9].subject)
        assertEquals("Metodologia programowania", full.getTimetable()[10].subject)
        assertEquals("Wychowanie do życia w rodzinie", full.getTimetable()[20].subject)
        assertEquals("Wychowanie fizyczne", full.getTimetable()[30].subject)
        assertEquals("Uroczyste zakończenie roku szkolnego", full.getTimetable()[37].subject)
        assertEquals("Język niemiecki", full.getTimetable()[39].subject)
    }

    @Test fun getLessonTeacherTest() {
        assertEquals("Bogatka Katarzyna", std.getTimetable()[1].teacher)
        assertEquals("Chlebowski Stanisław", std.getTimetable()[17].teacher)

        assertEquals("Kobczyk Iwona", full.getTimetable()[1].teacher)
        assertEquals("Bączek Grzegorz", full.getTimetable()[7].teacher)
        assertEquals("Nowak Jadwiga", full.getTimetable()[20].teacher)
        assertEquals("Nowicka Irena", full.getTimetable()[30].teacher)
        assertEquals("Baran Małgorzata", full.getTimetable()[37].teacher)
        assertEquals("", full.getTimetable()[38].teacher)
    }

    @Test fun getLessonRoomTest() {
        assertEquals("", std.getTimetable()[15].room)

        assertEquals("19", full.getTimetable()[0].room)
        assertEquals("33", full.getTimetable()[7].room)
        assertEquals("32", full.getTimetable()[10].room)
        assertEquals("32", full.getTimetable()[18].room)
        assertEquals("32", full.getTimetable()[28].room)
        assertEquals("G4", full.getTimetable()[30].room)
        assertEquals("37", full.getTimetable()[37].room)
    }

    @Test fun getLessonDescriptionTest() {
        assertEquals("", std.getTimetable()[15].description)

        assertEquals("okienko dla uczniów", full.getTimetable()[7].description)
        assertEquals("przeniesiona z lekcji 7, 01.12.2017", full.getTimetable()[11].description)
        assertEquals("przeniesiona z lekcji 7, 20.06.2017", full.getTimetable()[12].description)
        assertEquals("przeniesiona z lekcji 4, 20.06.2017", full.getTimetable()[13].description)
        assertEquals("zastępstwo (poprzednio: Religia)", full.getTimetable()[20].description)
        assertEquals("egzamin", full.getTimetable()[29].description)
        assertEquals("zastępstwo (poprzednio: Wychowanie fizyczne)", full.getTimetable()[30].description)
        assertEquals("", full.getTimetable()[37].description)
        assertEquals("", full.getTimetable()[38].description)
        assertEquals("poprzednio: Wychowanie fizyczne", full.getTimetable()[39].description)
        assertEquals("bez nawiasów (poprzednio: Religia)", full.getTimetable()[40].description)
        assertEquals("opis w uwadze bez klasy w spanie", full.getTimetable()[41].description)
        assertEquals("poprzednio: Zajęcia z wychowawcą", full.getTimetable()[42].description)
    }

    @Test fun getLessonGroupNameTest() {
        assertEquals("CH", std.getTimetable()[2].groupName)
        assertEquals("JNPW", std.getTimetable()[26].groupName)

        assertEquals("", full.getTimetable()[7].groupName)
        assertEquals("zaw2", full.getTimetable()[10].groupName)
        assertEquals("wf2", full.getTimetable()[13].groupName)
        assertEquals("zaw1", full.getTimetable()[30].groupName)
    }

    @Test fun getLessonStartTimeTest() {
        assertEquals("08:00", std.getTimetable()[0].startTime)
        assertEquals("13:20", std.getTimetable()[12].startTime)

        assertEquals("07:10", full.getTimetable()[0].startTime)
        assertEquals("12:20", full.getTimetable()[26].startTime)
    }

    @Test fun getLessonEndTimeTest() {
        assertEquals("08:45", std.getTimetable()[0].endTime)
        assertEquals("14:05", std.getTimetable()[12].endTime)

        assertEquals("07:55", full.getTimetable()[10].endTime)
        assertEquals("13:55", full.getTimetable()[36].endTime)
    }

    @Test fun getLessonIsEmptyTest() {
        assertFalse(std.getTimetable()[9].empty)

        assertFalse(full.getTimetable()[7].empty)
        assertFalse(full.getTimetable()[10].empty)
        assertFalse(full.getTimetable()[12].empty)
    }

    @Test fun getLessonIsDivisionIntoGroupsTest() {
        assertTrue(std.getTimetable()[2].divisionIntoGroups)
        assertTrue(std.getTimetable()[26].divisionIntoGroups)

        assertFalse(full.getTimetable()[7].divisionIntoGroups)
        assertTrue(full.getTimetable()[13].divisionIntoGroups)
        assertTrue(full.getTimetable()[30].divisionIntoGroups)
    }

    @Test fun getLessonIsPlanningTest() {
        assertFalse(std.getTimetable()[30].planning)

        assertFalse(full.getTimetable()[1].planning)
        assertTrue(full.getTimetable()[13].planning)
        assertTrue(full.getTimetable()[37].planning)
    }

    @Test fun getLessonIsRealizedTest() {
        assertTrue(std.getTimetable()[15].realized)

        assertTrue(full.getTimetable()[1].realized)
        assertTrue(full.getTimetable()[13].realized)
        assertFalse(full.getTimetable()[37].realized)
    }

    @Test fun getLessonIsMovedOrCanceledTest() {
        assertFalse(std.getTimetable()[15].movedOrCanceled)

        assertTrue(full.getTimetable()[7].movedOrCanceled)
        assertFalse(full.getTimetable()[13].movedOrCanceled)
        assertFalse(full.getTimetable()[37].movedOrCanceled)
    }

    @Test fun getLessonIsNewMovedInOrChangedTest() {
        assertFalse(std.getTimetable()[15].newMovedInOrChanged)

        assertFalse(full.getTimetable()[1].newMovedInOrChanged)
        assertTrue(full.getTimetable()[12].newMovedInOrChanged)
        assertTrue(full.getTimetable()[13].newMovedInOrChanged)
        assertTrue(full.getTimetable()[30].newMovedInOrChanged)
        assertFalse(full.getTimetable()[38].newMovedInOrChanged)
        assertTrue(full.getTimetable()[39].newMovedInOrChanged)
    }
}
