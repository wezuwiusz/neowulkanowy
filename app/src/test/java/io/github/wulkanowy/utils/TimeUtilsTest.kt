package io.github.wulkanowy.utils

import org.junit.Test
import org.threeten.bp.LocalDate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class TimeUtilsTest {

    @Test fun getParsedDateTest() {
        assertEquals(LocalDate.of(1970, 1, 1),
                getParsedDate("1970-01-01", "yyyy-MM-dd"))
    }

    @Test fun getMondaysFromCurrentSchoolYearTest() {
        val y201718 = getMondaysFromCurrentSchoolYear(LocalDate.of(2018, 1, 1))
        assertEquals("2017-09-04", y201718.first())
        assertEquals("2018-08-27", y201718.last())

        val y202122 = getMondaysFromCurrentSchoolYear(LocalDate.of(2022, 1, 1))
        assertEquals("2021-08-30", y202122.first())
        assertEquals("2022-08-22", y202122.last())

        val y202223 = getMondaysFromCurrentSchoolYear(LocalDate.of(2023, 1, 1))
        assertEquals("2022-08-29", y202223.first())
        assertEquals("2023-08-28", y202223.last())
    }

    @Test fun getCurrentSchoolYearTest() {
        assertEquals(2017, getSchoolYearForDate(LocalDate.of(2018, 8, 31)))
        assertEquals(2018, getSchoolYearForDate(LocalDate.of(2018, 9, 1)))
    }

    @Test fun getFirstWeekDayTest() {
        assertEquals("2018-06-18", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 21)))
        assertEquals("2018-06-18", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 22)))
        assertEquals("2018-06-25", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 23)))
        assertEquals("2018-06-25", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 24)))
        assertEquals("2018-06-25", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 25)))
        assertEquals("2018-06-25", getFirstDayOfCurrentWeek(LocalDate.of(2018, 6, 26)))
    }

    @Test fun getTodayOrNextDayOrderTest() {
        assertEquals(0, getTodayOrNextDayOrder(true, LocalDate.of(2018, 6, 24))) // sunday
        assertEquals(6, getTodayOrNextDayOrder(false, LocalDate.of(2018, 6, 24)))
        assertEquals(1, getTodayOrNextDayOrder(true, LocalDate.of(2018, 6, 25)))
        assertEquals(0, getTodayOrNextDayOrder(false, LocalDate.of(2018, 6, 25)))
    }

    @Test fun getTodayOrNextDayTest() {
        assertEquals("2018-06-26", getTodayOrNextDay(false, LocalDate.of(2018, 6, 26)))
        assertEquals("2018-06-27", getTodayOrNextDay(true, LocalDate.of(2018, 6, 26)))
    }

    @Test fun isDateInWeekInsideTest() {
        assertTrue(isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 31)
        ))
    }

    @Test fun isDateInWeekExtremesTest() {
        assertTrue(isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 28)
        ))

        assertTrue(isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 1)
        ))
    }

    @Test fun isDateInWeekOutOfTest() {
        assertFalse(isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 2)
        ))

        assertFalse(isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 27)
        ))
    }

    @Test fun isHolidaysInSchoolEndTest() {
        assertFalse(isHolidays(LocalDate.of(2017, 6, 23), 2017))
        assertFalse(isHolidays(LocalDate.of(2018, 6, 22), 2018))
        assertFalse(isHolidays(LocalDate.of(2019, 6, 21), 2019))
        assertFalse(isHolidays(LocalDate.of(2020, 6, 26), 2020))
        assertFalse(isHolidays(LocalDate.of(2021, 6, 25), 2021))
        assertFalse(isHolidays(LocalDate.of(2022, 6, 24), 2022))
        assertFalse(isHolidays(LocalDate.of(2023, 6, 23), 2023))
        assertFalse(isHolidays(LocalDate.of(2024, 6, 21), 2024))
        assertFalse(isHolidays(LocalDate.of(2025, 6, 27), 2025))
    }

    @Test fun isHolidaysInHolidaysStartTest() {
        assertTrue(isHolidays(LocalDate.of(2017, 6, 24), 2017))
        assertTrue(isHolidays(LocalDate.of(2018, 6, 23), 2018))
        assertTrue(isHolidays(LocalDate.of(2019, 6, 22), 2019))
        assertTrue(isHolidays(LocalDate.of(2020, 6, 27), 2020))
        assertTrue(isHolidays(LocalDate.of(2021, 6, 26), 2021))
        assertTrue(isHolidays(LocalDate.of(2022, 6, 25), 2022))
        assertTrue(isHolidays(LocalDate.of(2023, 6, 24), 2023))
        assertTrue(isHolidays(LocalDate.of(2024, 6, 22), 2024))
        assertTrue(isHolidays(LocalDate.of(2025, 6, 28), 2025))
    }

    @Test fun isHolidaysInHolidaysEndTest() {
        assertTrue(isHolidays(LocalDate.of(2017, 9, 1), 2017)) // friday
        assertTrue(isHolidays(LocalDate.of(2017, 9, 2), 2017)) // saturday
        assertTrue(isHolidays(LocalDate.of(2017, 9, 3), 2017)) // sunday
        assertTrue(isHolidays(LocalDate.of(2018, 9, 1), 2018)) // saturday
        assertTrue(isHolidays(LocalDate.of(2018, 9, 2), 2018)) // sunday
        assertTrue(isHolidays(LocalDate.of(2019, 9, 1), 2019)) // sunday
        assertTrue(isHolidays(LocalDate.of(2020, 8, 31), 2020)) // monday
        assertTrue(isHolidays(LocalDate.of(2021, 8, 31), 2021)) // tuesday
        assertTrue(isHolidays(LocalDate.of(2022, 8, 31), 2022)) // wednesday
        assertTrue(isHolidays(LocalDate.of(2023, 9, 1), 2023)) // friday
        assertTrue(isHolidays(LocalDate.of(2023, 9, 2), 2023)) // saturday
        assertTrue(isHolidays(LocalDate.of(2023, 9, 3), 2023)) // sunday
        assertTrue(isHolidays(LocalDate.of(2024, 9, 1), 2024)) // sunday
        assertTrue(isHolidays(LocalDate.of(2025, 8, 31), 2025)) // sunday
    }

    @Test fun isHolidaysInSchoolStartTest() {
        assertFalse(isHolidays(LocalDate.of(2017, 9, 4), 2017)) // monday
        assertFalse(isHolidays(LocalDate.of(2018, 9, 3), 2018)) // monday
        assertFalse(isHolidays(LocalDate.of(2019, 9, 2), 2019)) // monday
        assertFalse(isHolidays(LocalDate.of(2020, 9, 1), 2020)) // tuesday
        assertFalse(isHolidays(LocalDate.of(2021, 9, 1), 2021)) // wednesday
        assertFalse(isHolidays(LocalDate.of(2022, 9, 1), 2022)) // thursday
        assertFalse(isHolidays(LocalDate.of(2023, 9, 4), 2023)) // monday
        assertFalse(isHolidays(LocalDate.of(2024, 9, 2), 2024)) // monday
        assertFalse(isHolidays(LocalDate.of(2025, 9, 1), 2025)) // monday
    }
}
