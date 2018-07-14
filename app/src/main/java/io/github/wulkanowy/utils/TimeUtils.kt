package io.github.wulkanowy.utils

import org.threeten.bp.DayOfWeek.*
import org.threeten.bp.LocalDate
import org.threeten.bp.Year
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters
import java.util.*

private val formatter = DateTimeFormatter.ofPattern(AppConstant.DATE_PATTERN)

fun getParsedDate(dateString: String, dateFormat: String): LocalDate {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat))
}

fun getMondaysFromCurrentSchoolYear() = getMondaysFromCurrentSchoolYear(LocalDate.now())

fun getMondaysFromCurrentSchoolYear(date: LocalDate): List<String> {
    val startDate = getFirstSchoolDay(getSchoolYearForDate(date))
            ?.with(TemporalAdjusters.previousOrSame(MONDAY))
    val endDate = getFirstSchoolDay(getSchoolYearForDate(date) + 1)
            ?.with(TemporalAdjusters.previousOrSame(MONDAY))

    val dateList = ArrayList<String>()
    var monday = startDate as LocalDate
    while (monday.isBefore(endDate)) {
        dateList.add(monday.format(formatter))
        monday = monday.plusWeeks(1)
    }

    return dateList
}

fun getSchoolYearForDate(date: LocalDate): Int {
    return if (date.monthValue <= 8) date.year - 1 else date.year
}

fun getFirstDayOfCurrentWeek(): String = getFirstDayOfCurrentWeek(LocalDate.now())

fun getFirstDayOfCurrentWeek(date: LocalDate): String {
    return when (date.dayOfWeek) {
        SATURDAY -> date.plusDays(2)
        SUNDAY -> date.plusDays(1)
        else -> date.with(MONDAY)
    }.format(formatter)
}

fun getTodayOrNextDayOrder(next: Boolean): Int = getTodayOrNextDayOrder(next, LocalDate.now())

fun getTodayOrNextDayOrder(next: Boolean, date: LocalDate): Int {
    val day = date.dayOfWeek
    return if (next) {
        if (day == SUNDAY) {
            0
        } else day.value
    } else day.value - 1
}

fun getTodayOrNextDay(next: Boolean): String? = getTodayOrNextDay(next, LocalDate.now())

fun getTodayOrNextDay(next: Boolean, date: LocalDate): String? {
    return (if (next) {
        date.plusDays(1)
    } else date).format(formatter)
}

fun isDateInWeek(firstWeekDay: LocalDate, date: LocalDate): Boolean {
    return date.isAfter(firstWeekDay.minusDays(1)) && date.isBefore(firstWeekDay.plusDays(5))
}

/**
 * [Dz.U. 2016 poz. 1335](http://prawo.sejm.gov.pl/isap.nsf/DocDetails.xsp?id=WDU20160001335)
 */
fun isHolidays(): Boolean = isHolidays(LocalDate.now(), Year.now().value)

fun isHolidays(day: LocalDate, year: Int): Boolean {
    return day.isAfter(getLastSchoolDay(year)) && day.isBefore(getFirstSchoolDay(year))
}

fun getFirstSchoolDay(year: Int): LocalDate? {
    val firstSeptember = LocalDate.of(year, 9, 1)

    return when (firstSeptember.dayOfWeek) {
        FRIDAY,
        SATURDAY,
        SUNDAY -> firstSeptember.with(TemporalAdjusters.firstInMonth(MONDAY))
        else -> {
            firstSeptember
        }
    }
}

fun getLastSchoolDay(year: Int): LocalDate? {
    return LocalDate
            .of(year, 6, 20)
            .with(TemporalAdjusters.next(FRIDAY))
}
