package io.github.wulkanowy.utils

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.*

class TimeExtensionTest {

    @Test
    fun toLocalDateTest() {
        assertEquals(LocalDate.of(1970, 1, 1), "1970-01-01".toLocalDate("yyyy-MM-dd"))
    }

    @Test
    fun toFormattedStringTest() {
        assertEquals("2018-10-01", LocalDate.of(2018, 10, 1).toFormattedString())
        assertEquals("2018-10.01", LocalDate.of(2018, 10, 1).toFormattedString("yyyy-MM.dd"))
    }

    @Test
    fun weekFirstDayAlwaysCurrentTest() {
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2).weekFirstDayAlwaysCurrent)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 5).weekFirstDayAlwaysCurrent)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 6).weekFirstDayAlwaysCurrent)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 7).weekFirstDayAlwaysCurrent)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 8).weekFirstDayAlwaysCurrent)
    }

    @Test
    fun weekFirstDayNextOnWeekEndTest() {
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2).weekFirstDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 5).weekFirstDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 6).weekFirstDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 7).weekFirstDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 8).weekFirstDayNextOnWeekEnd)
    }

    @Test
    fun weekDayNameTest() {
        Locale.setDefault(Locale.forLanguageTag("PL"))
        assertEquals("poniedziałek", LocalDate.of(2018, 10, 1).weekDayName)
        Locale.setDefault(Locale.forLanguageTag("US"))
        assertEquals("Monday", LocalDate.of(2018, 10, 1).weekDayName)
    }

    @Test
    fun nextSchoolDayTest() {
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 1).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 2).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 3).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 4).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 5).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 6).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 7).nextWorkDay)
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 8).nextWorkDay)
    }

    @Test
    fun previousSchoolDayTest() {
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 10).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 9).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 8).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 7).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 6).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 5).previousWorkDay)
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 4).previousWorkDay)
    }

    @Test
    fun nearSchoolDayPrevOnWeekEndTest() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).nearSchoolDayPrevOnWeekEnd)
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 29).nearSchoolDayPrevOnWeekEnd)
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 30).nearSchoolDayPrevOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).nearSchoolDayPrevOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).nearSchoolDayPrevOnWeekEnd)
    }

    @Test
    fun nearSchoolDayNextOnWeekEndTest() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).nearSchoolDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 29).nearSchoolDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 30).nearSchoolDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).nearSchoolDayNextOnWeekEnd)
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).nearSchoolDayNextOnWeekEnd)
    }

    @Test
    fun isHolidays_schoolEndTest() {
        assertFalse(LocalDate.of(2017, 6, 23).isHolidays)
        assertFalse(LocalDate.of(2018, 6, 22).isHolidays)
        assertFalse(LocalDate.of(2019, 6, 21).isHolidays)
        assertFalse(LocalDate.of(2020, 6, 26).isHolidays)
        assertFalse(LocalDate.of(2021, 6, 25).isHolidays)
        assertFalse(LocalDate.of(2022, 6, 24).isHolidays)
        assertFalse(LocalDate.of(2023, 6, 23).isHolidays)
        assertFalse(LocalDate.of(2024, 6, 21).isHolidays)
        assertFalse(LocalDate.of(2025, 6, 27).isHolidays)
    }

    @Test
    fun isHolidays_holidaysStartTest() {
        assertTrue(LocalDate.of(2017, 6, 24).isHolidays)
        assertTrue(LocalDate.of(2018, 6, 23).isHolidays)
        assertTrue(LocalDate.of(2019, 6, 22).isHolidays)
        assertTrue(LocalDate.of(2020, 6, 27).isHolidays)
        assertTrue(LocalDate.of(2021, 6, 26).isHolidays)
        assertTrue(LocalDate.of(2022, 6, 25).isHolidays)
        assertTrue(LocalDate.of(2023, 6, 24).isHolidays)
        assertTrue(LocalDate.of(2024, 6, 22).isHolidays)
        assertTrue(LocalDate.of(2025, 6, 28).isHolidays)
    }

    @Test
    fun isHolidays_holidaysEndTest() {
        assertTrue(LocalDate.of(2017, 9, 1).isHolidays) // friday
        assertTrue(LocalDate.of(2017, 9, 2).isHolidays) // saturday
        assertTrue(LocalDate.of(2017, 9, 3).isHolidays) // sunday
        assertTrue(LocalDate.of(2018, 9, 1).isHolidays) // saturday
        assertTrue(LocalDate.of(2018, 9, 2).isHolidays) // sunday
        assertTrue(LocalDate.of(2019, 9, 1).isHolidays) // sunday
        assertTrue(LocalDate.of(2020, 8, 31).isHolidays) // monday
        assertTrue(LocalDate.of(2021, 8, 31).isHolidays) // tuesday
        assertTrue(LocalDate.of(2022, 8, 31).isHolidays) // wednesday
        assertTrue(LocalDate.of(2023, 9, 1).isHolidays) // friday
        assertTrue(LocalDate.of(2023, 9, 2).isHolidays) // saturday
        assertTrue(LocalDate.of(2023, 9, 3).isHolidays) // sunday
        assertTrue(LocalDate.of(2024, 9, 1).isHolidays) // sunday
        assertTrue(LocalDate.of(2025, 8, 31).isHolidays) // sunday
    }

    @Test
    fun isHolidays_schoolStartTest() {
        assertFalse(LocalDate.of(2017, 9, 4).isHolidays) // monday
        assertFalse(LocalDate.of(2018, 9, 3).isHolidays) // monday
        assertFalse(LocalDate.of(2019, 9, 2).isHolidays) // monday
        assertFalse(LocalDate.of(2020, 9, 1).isHolidays) // tuesday
        assertFalse(LocalDate.of(2021, 9, 1).isHolidays) // wednesday
        assertFalse(LocalDate.of(2022, 9, 1).isHolidays) // thursday
        assertFalse(LocalDate.of(2023, 9, 4).isHolidays) // monday
        assertFalse(LocalDate.of(2024, 9, 2).isHolidays) // monday
        assertFalse(LocalDate.of(2025, 9, 1).isHolidays) // monday
    }
}
