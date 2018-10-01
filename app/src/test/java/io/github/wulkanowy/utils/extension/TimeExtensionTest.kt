package io.github.wulkanowy.utils.extension

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.*

class TimeExtensionTest {

    @Test
    fun toDate() {
        assertEquals(LocalDate.of(1970, 1, 1), "1970-01-01".toDate("yyyy-MM-dd"))
    }

    @Test
    fun toFormat() {
        assertEquals("2018-10-01", LocalDate.of(2018, 10, 1).toFormat())
        assertEquals("2018-10.01", LocalDate.of(2018, 10, 1).toFormat("yyyy-MM.dd"))
    }

    @Test
    fun getWeekFirstDayAlwaysCurrent() {
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2).getWeekFirstDayAlwaysCurrent())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 5).getWeekFirstDayAlwaysCurrent())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 6).getWeekFirstDayAlwaysCurrent())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 7).getWeekFirstDayAlwaysCurrent())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 8).getWeekFirstDayAlwaysCurrent())
    }

    @Test
    fun getWeekFirstDayNextOnWeekEnd() {
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2).getWeekFirstDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 5).getWeekFirstDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 6).getWeekFirstDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 7).getWeekFirstDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 8).getWeekFirstDayNextOnWeekEnd())
    }

    @Test
    fun getWeekDayName() {
        Locale.setDefault(Locale.forLanguageTag("PL"))
        assertEquals("poniedziałek", LocalDate.of(2018, 10, 1).getWeekDayName())
        Locale.setDefault(Locale.forLanguageTag("US"))
        assertEquals("Monday", LocalDate.of(2018, 10, 1).getWeekDayName())
    }

    @Test
    fun getSchoolYear() {
        assertEquals(2017, LocalDate.of(2018, 8, 31).getSchoolYear())
        assertEquals(2018, LocalDate.of(2018, 9, 1).getSchoolYear())
    }

    @Test
    fun getNextSchoolDay() {
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 1).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 2).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 3).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 4).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 5).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 6).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 7).getNextWorkDay())
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 8).getNextWorkDay())
    }

    @Test
    fun getPreviousSchoolDay() {
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 10).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 9).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 8).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 7).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 6).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 5).getPreviousWorkDay())
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 4).getPreviousWorkDay())
    }

    @Test
    fun getNearSchoolDayPrevOnWeekEnd() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).getNearSchoolDayPrevOnWeekEnd())
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 29).getNearSchoolDayPrevOnWeekEnd())
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 30).getNearSchoolDayPrevOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).getNearSchoolDayPrevOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).getNearSchoolDayPrevOnWeekEnd())
    }

    @Test
    fun getNearSchoolDayNextOnWeekEnd() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).getNearSchoolDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 29).getNearSchoolDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 30).getNearSchoolDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).getNearSchoolDayNextOnWeekEnd())
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).getNearSchoolDayNextOnWeekEnd())
    }

    @Test
    fun isHolidays_schoolEnd() {
        assertFalse(LocalDate.of(2017, 6, 23).isHolidays())
        assertFalse(LocalDate.of(2018, 6, 22).isHolidays())
        assertFalse(LocalDate.of(2019, 6, 21).isHolidays())
        assertFalse(LocalDate.of(2020, 6, 26).isHolidays())
        assertFalse(LocalDate.of(2021, 6, 25).isHolidays())
        assertFalse(LocalDate.of(2022, 6, 24).isHolidays())
        assertFalse(LocalDate.of(2023, 6, 23).isHolidays())
        assertFalse(LocalDate.of(2024, 6, 21).isHolidays())
        assertFalse(LocalDate.of(2025, 6, 27).isHolidays())
    }

    @Test
    fun isHolidays_holidaysStart() {
        assertTrue(LocalDate.of(2017, 6, 24).isHolidays())
        assertTrue(LocalDate.of(2018, 6, 23).isHolidays())
        assertTrue(LocalDate.of(2019, 6, 22).isHolidays())
        assertTrue(LocalDate.of(2020, 6, 27).isHolidays())
        assertTrue(LocalDate.of(2021, 6, 26).isHolidays())
        assertTrue(LocalDate.of(2022, 6, 25).isHolidays())
        assertTrue(LocalDate.of(2023, 6, 24).isHolidays())
        assertTrue(LocalDate.of(2024, 6, 22).isHolidays())
        assertTrue(LocalDate.of(2025, 6, 28).isHolidays())
    }

    @Test
    fun isHolidays_holidaysEnd() {
        assertTrue(LocalDate.of(2017, 9, 1).isHolidays()) // friday
        assertTrue(LocalDate.of(2017, 9, 2).isHolidays()) // saturday
        assertTrue(LocalDate.of(2017, 9, 3).isHolidays()) // sunday
        assertTrue(LocalDate.of(2018, 9, 1).isHolidays()) // saturday
        assertTrue(LocalDate.of(2018, 9, 2).isHolidays()) // sunday
        assertTrue(LocalDate.of(2019, 9, 1).isHolidays()) // sunday
        assertTrue(LocalDate.of(2020, 8, 31).isHolidays()) // monday
        assertTrue(LocalDate.of(2021, 8, 31).isHolidays()) // tuesday
        assertTrue(LocalDate.of(2022, 8, 31).isHolidays()) // wednesday
        assertTrue(LocalDate.of(2023, 9, 1).isHolidays()) // friday
        assertTrue(LocalDate.of(2023, 9, 2).isHolidays()) // saturday
        assertTrue(LocalDate.of(2023, 9, 3).isHolidays()) // sunday
        assertTrue(LocalDate.of(2024, 9, 1).isHolidays()) // sunday
        assertTrue(LocalDate.of(2025, 8, 31).isHolidays()) // sunday
    }

    @Test
    fun isHolidays_schoolStart() {
        assertFalse(LocalDate.of(2017, 9, 4).isHolidays()) // monday
        assertFalse(LocalDate.of(2018, 9, 3).isHolidays()) // monday
        assertFalse(LocalDate.of(2019, 9, 2).isHolidays()) // monday
        assertFalse(LocalDate.of(2020, 9, 1).isHolidays()) // tuesday
        assertFalse(LocalDate.of(2021, 9, 1).isHolidays()) // wednesday
        assertFalse(LocalDate.of(2022, 9, 1).isHolidays()) // thursday
        assertFalse(LocalDate.of(2023, 9, 4).isHolidays()) // monday
        assertFalse(LocalDate.of(2024, 9, 2).isHolidays()) // monday
        assertFalse(LocalDate.of(2025, 9, 1).isHolidays()) // monday
    }
}
