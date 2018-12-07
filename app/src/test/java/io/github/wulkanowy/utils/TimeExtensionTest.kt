package io.github.wulkanowy.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month.JANUARY
import java.util.Locale

class TimeExtensionTest {

    @Test
    fun toLocalDateTest() {
        assertEquals(LocalDate.of(1970, 1, 1), "1970-01-01".toLocalDate("yyyy-MM-dd"))
    }

    @Test
    fun toFormattedStringLocalDateTest() {
        assertEquals("01.10.2018", LocalDate.of(2018, 10, 1).toFormattedString())
        assertEquals("2018-10.01", LocalDate.of(2018, 10, 1).toFormattedString("yyyy-MM.dd"))
    }

    @Test
    fun toFormattedStringLocalDateTimeTest() {
        assertEquals("01.10.2018", LocalDateTime.of(2018, 10, 1, 10, 0, 0).toFormattedString())
        assertEquals("2018-10-01 10:00:00", LocalDateTime.of(2018, 10, 1, 10, 0, 0).toFormattedString("uuuu-MM-dd HH:mm:ss"))
    }

    @Test
    fun mondayTest() {
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2).monday)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 5).monday)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 6).monday)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 7).monday)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 8).monday)
    }

    @Test
    fun fridayTest() {
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 2).friday)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 5).friday)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 6).friday)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 7).friday)
        assertEquals(LocalDate.of(2018, 10, 12), LocalDate.of(2018, 10, 8).friday)
    }

    @Test
    fun monthNameTest() {
        Locale.setDefault(Locale.forLanguageTag("PL"))
        assertEquals("Styczeń", JANUARY.getFormattedName())
        Locale.setDefault(Locale.forLanguageTag("US"))
        assertEquals("January", JANUARY.getFormattedName())
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
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 1).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 2).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 3).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 4).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 5).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 6).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 7).nextSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 8).nextSchoolDay)
    }

    @Test
    fun previousSchoolDayTest() {
        assertEquals(LocalDate.of(2018, 10, 9), LocalDate.of(2018, 10, 10).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 8), LocalDate.of(2018, 10, 9).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 8).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 7).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 5), LocalDate.of(2018, 10, 6).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 4), LocalDate.of(2018, 10, 5).previousSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 3), LocalDate.of(2018, 10, 4).previousSchoolDay)
    }

    @Test
    fun nextOrSameSchoolDayTest() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).nextOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 29).nextOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 9, 30).nextOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).nextOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).nextOrSameSchoolDay)
    }

    @Test
    fun previousOrSameSchoolDayTest() {
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 28).previousOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 29).previousOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 9, 28), LocalDate.of(2018, 9, 30).previousOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 1).previousOrSameSchoolDay)
        assertEquals(LocalDate.of(2018, 10, 2), LocalDate.of(2018, 10, 2).previousOrSameSchoolDay)
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
