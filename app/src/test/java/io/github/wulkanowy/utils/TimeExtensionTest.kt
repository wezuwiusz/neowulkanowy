package io.github.wulkanowy.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month.JANUARY
import java.util.Locale

class TimeExtensionTest {

    @Test
    fun toLocalDateTest() {
        assertEquals(of(1970, 1, 1), "1970-01-01".toLocalDate("yyyy-MM-dd"))
    }

    @Test
    fun toFormattedStringLocalDateTest() {
        assertEquals("01.10.2018", of(2018, 10, 1).toFormattedString())
        assertEquals("2018-10.01", of(2018, 10, 1).toFormattedString("yyyy-MM.dd"))
    }

    @Test
    fun toFormattedStringLocalDateTimeTest() {
        assertEquals("01.10.2018", LocalDateTime.of(2018, 10, 1, 10, 0, 0).toFormattedString())
        assertEquals("2018-10-01 10:00:00", LocalDateTime.of(2018, 10, 1, 10, 0, 0).toFormattedString("uuuu-MM-dd HH:mm:ss"))
    }

    @Test
    fun mondayTest() {
        assertEquals(of(2018, 10, 1), of(2018, 10, 2).monday)
        assertEquals(of(2018, 10, 1), of(2018, 10, 5).monday)
        assertEquals(of(2018, 10, 1), of(2018, 10, 6).monday)
        assertEquals(of(2018, 10, 1), of(2018, 10, 7).monday)
        assertEquals(of(2018, 10, 8), of(2018, 10, 8).monday)
    }

    @Test
    fun fridayTest() {
        assertEquals(of(2018, 10, 5), of(2018, 10, 2).friday)
        assertEquals(of(2018, 10, 5), of(2018, 10, 5).friday)
        assertEquals(of(2018, 10, 5), of(2018, 10, 6).friday)
        assertEquals(of(2018, 10, 5), of(2018, 10, 7).friday)
        assertEquals(of(2018, 10, 12), of(2018, 10, 8).friday)
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
        assertEquals("poniedziałek", of(2018, 10, 1).weekDayName)
        Locale.setDefault(Locale.forLanguageTag("US"))
        assertEquals("Monday", of(2018, 10, 1).weekDayName)
    }

    @Test
    fun nextSchoolDayTest() {
        assertEquals(of(2018, 10, 2), of(2018, 10, 1).nextSchoolDay)
        assertEquals(of(2018, 10, 3), of(2018, 10, 2).nextSchoolDay)
        assertEquals(of(2018, 10, 4), of(2018, 10, 3).nextSchoolDay)
        assertEquals(of(2018, 10, 5), of(2018, 10, 4).nextSchoolDay)
        assertEquals(of(2018, 10, 8), of(2018, 10, 5).nextSchoolDay)
        assertEquals(of(2018, 10, 8), of(2018, 10, 6).nextSchoolDay)
        assertEquals(of(2018, 10, 8), of(2018, 10, 7).nextSchoolDay)
        assertEquals(of(2018, 10, 9), of(2018, 10, 8).nextSchoolDay)
    }

    @Test
    fun previousSchoolDayTest() {
        assertEquals(of(2018, 10, 9), of(2018, 10, 10).previousSchoolDay)
        assertEquals(of(2018, 10, 8), of(2018, 10, 9).previousSchoolDay)
        assertEquals(of(2018, 10, 5), of(2018, 10, 8).previousSchoolDay)
        assertEquals(of(2018, 10, 5), of(2018, 10, 7).previousSchoolDay)
        assertEquals(of(2018, 10, 5), of(2018, 10, 6).previousSchoolDay)
        assertEquals(of(2018, 10, 4), of(2018, 10, 5).previousSchoolDay)
        assertEquals(of(2018, 10, 3), of(2018, 10, 4).previousSchoolDay)
    }

    @Test
    fun nextOrSameSchoolDayTest() {
        assertEquals(of(2018, 9, 28), of(2018, 9, 28).nextOrSameSchoolDay)
        assertEquals(of(2018, 10, 1), of(2018, 9, 29).nextOrSameSchoolDay)
        assertEquals(of(2018, 10, 1), of(2018, 9, 30).nextOrSameSchoolDay)
        assertEquals(of(2018, 10, 1), of(2018, 10, 1).nextOrSameSchoolDay)
        assertEquals(of(2018, 10, 2), of(2018, 10, 2).nextOrSameSchoolDay)
    }

    @Test
    fun previousOrSameSchoolDayTest() {
        assertEquals(of(2018, 9, 28), of(2018, 9, 28).previousOrSameSchoolDay)
        assertEquals(of(2018, 9, 28), of(2018, 9, 29).previousOrSameSchoolDay)
        assertEquals(of(2018, 9, 28), of(2018, 9, 30).previousOrSameSchoolDay)
        assertEquals(of(2018, 10, 1), of(2018, 10, 1).previousOrSameSchoolDay)
        assertEquals(of(2018, 10, 2), of(2018, 10, 2).previousOrSameSchoolDay)
    }

    @Test
    fun isHolidays_schoolEndTest() {
        assertFalse(of(2017, 6, 23).isHolidays)
        assertFalse(of(2018, 6, 22).isHolidays)
        assertFalse(of(2019, 6, 21).isHolidays)
        assertFalse(of(2020, 6, 26).isHolidays)
        assertFalse(of(2021, 6, 25).isHolidays)
        assertFalse(of(2022, 6, 24).isHolidays)
        assertFalse(of(2023, 6, 23).isHolidays)
        assertFalse(of(2024, 6, 21).isHolidays)
        assertFalse(of(2025, 6, 27).isHolidays)
    }

    @Test
    fun isHolidays_holidaysStartTest() {
        assertTrue(of(2017, 6, 24).isHolidays)
        assertTrue(of(2018, 6, 23).isHolidays)
        assertTrue(of(2019, 6, 22).isHolidays)
        assertTrue(of(2020, 6, 27).isHolidays)
        assertTrue(of(2021, 6, 26).isHolidays)
        assertTrue(of(2022, 6, 25).isHolidays)
        assertTrue(of(2023, 6, 24).isHolidays)
        assertTrue(of(2024, 6, 22).isHolidays)
        assertTrue(of(2025, 6, 28).isHolidays)
    }

    @Test
    fun isHolidays_holidaysEndTest() {
        assertTrue(of(2017, 9, 1).isHolidays) // friday
        assertTrue(of(2017, 9, 2).isHolidays) // saturday
        assertTrue(of(2017, 9, 3).isHolidays) // sunday
        assertTrue(of(2018, 9, 1).isHolidays) // saturday
        assertTrue(of(2018, 9, 2).isHolidays) // sunday
        assertTrue(of(2019, 9, 1).isHolidays) // sunday
        assertTrue(of(2020, 8, 31).isHolidays) // monday
        assertTrue(of(2021, 8, 31).isHolidays) // tuesday
        assertTrue(of(2022, 8, 31).isHolidays) // wednesday
        assertTrue(of(2023, 9, 1).isHolidays) // friday
        assertTrue(of(2023, 9, 2).isHolidays) // saturday
        assertTrue(of(2023, 9, 3).isHolidays) // sunday
        assertTrue(of(2024, 9, 1).isHolidays) // sunday
        assertTrue(of(2025, 8, 31).isHolidays) // sunday
    }

    @Test
    fun isHolidays_schoolStartTest() {
        assertFalse(of(2017, 9, 4).isHolidays) // monday
        assertFalse(of(2018, 9, 3).isHolidays) // monday
        assertFalse(of(2019, 9, 2).isHolidays) // monday
        assertFalse(of(2020, 9, 1).isHolidays) // tuesday
        assertFalse(of(2021, 9, 1).isHolidays) // wednesday
        assertFalse(of(2022, 9, 1).isHolidays) // thursday
        assertFalse(of(2023, 9, 4).isHolidays) // monday
        assertFalse(of(2024, 9, 2).isHolidays) // monday
        assertFalse(of(2025, 9, 1).isHolidays) // monday
    }

    @Test
    fun getCorrectedDate_holidays() {
        assertEquals(of(2019, 6, 21), of(2019, 8, 9).getLastSchoolDayIfHoliday(2018))
        assertEquals(of(2018, 6, 22), of(2019, 8, 9).getLastSchoolDayIfHoliday(2017))
    }

    @Test
    fun getCorrectedDate_schoolYear() {
        assertEquals(of(2019, 5, 1), of(2019, 5, 1).getLastSchoolDayIfHoliday(2018))
        assertEquals(of(2018, 5, 1), of(2019, 5, 1).getLastSchoolDayIfHoliday(2017))
    }
}
