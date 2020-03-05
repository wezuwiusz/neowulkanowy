package io.github.wulkanowy.utils

import org.threeten.bp.DayOfWeek.FRIDAY
import org.threeten.bp.DayOfWeek.MONDAY
import org.threeten.bp.DayOfWeek.SATURDAY
import org.threeten.bp.DayOfWeek.SUNDAY
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.format.TextStyle.FULL_STANDALONE
import org.threeten.bp.temporal.TemporalAdjusters.firstInMonth
import org.threeten.bp.temporal.TemporalAdjusters.next
import org.threeten.bp.temporal.TemporalAdjusters.previous
import java.util.Locale

private const val DATE_PATTERN = "dd.MM.yyyy"

fun String.toLocalDate(format: String = DATE_PATTERN): LocalDate = LocalDate.parse(this, ofPattern(format))

fun LocalDate.toFormattedString(format: String = DATE_PATTERN): String = format(ofPattern(format))

fun LocalDateTime.toFormattedString(format: String = DATE_PATTERN): String = format(ofPattern(format))

/**
 * https://github.com/ThreeTen/threetenbp/issues/55
 */
fun Month.getFormattedName(): String {
    return getDisplayName(FULL_STANDALONE, Locale.getDefault())
        .let {
            when (it) {
                "stycznia" -> "Styczeń"
                "lutego" -> "Luty"
                "marca" -> "Marzec"
                "kwietnia" -> "Kwiecień"
                "maja" -> "Maj"
                "czerwca" -> "Czerwiec"
                "lipca" -> "Lipiec"
                "sierpnia" -> "Sierpień"
                "września" -> "Wrzesień"
                "października" -> "Październik"
                "listopada" -> "Listopad"
                "grudnia" -> "Grudzień"
                else -> it
            }
        }
}

inline val LocalDate.nextSchoolDay: LocalDate
    get() {
        return when (dayOfWeek) {
            FRIDAY, SATURDAY, SUNDAY -> with(next(MONDAY))
            else -> plusDays(1)
        }
    }

inline val LocalDate.previousSchoolDay: LocalDate
    get() {
        return when (dayOfWeek) {
            SATURDAY, SUNDAY, MONDAY -> with(previous(FRIDAY))
            else -> minusDays(1)
        }
    }

inline val LocalDate.nextOrSameSchoolDay: LocalDate
    get() {
        return when (dayOfWeek) {
            SATURDAY, SUNDAY -> with(next(MONDAY))
            else -> this
        }
    }

inline val LocalDate.previousOrSameSchoolDay: LocalDate
    get() {
        return when (dayOfWeek) {
            SATURDAY, SUNDAY -> with(previous(FRIDAY))
            else -> this
        }
    }

inline val LocalDate.weekDayName: String
    get() = format(ofPattern("EEEE", Locale.getDefault()))

inline val LocalDate.monday: LocalDate
    get() = with(MONDAY)

inline val LocalDate.friday: LocalDate
    get() = with(FRIDAY)

/**
 * [Dz.U. 2016 poz. 1335](http://prawo.sejm.gov.pl/isap.nsf/DocDetails.xsp?id=WDU20160001335)
 */
inline val LocalDate.isHolidays: Boolean
    get() = isBefore(firstSchoolDay) && isAfter(lastSchoolDay)

inline val LocalDate.firstSchoolDay: LocalDate
    get() = LocalDate.of(year, 9, 1).run {
        when (dayOfWeek) {
            FRIDAY, SATURDAY, SUNDAY -> with(firstInMonth(MONDAY))
            else -> this
        }
    }

inline val LocalDate.lastSchoolDay: LocalDate
    get() = LocalDate.of(year, 6, 20)
        .with(next(FRIDAY))

private fun Int.getSchoolYearByMonth(monthValue: Int): Int {
    return when (monthValue) {
        in 9..12 -> this
        else -> this + 1
    }
}

fun LocalDate.getLastSchoolDayIfHoliday(schoolYear: Int): LocalDate {
    val date = LocalDate.of(schoolYear.getSchoolYearByMonth(monthValue), monthValue, dayOfMonth)

    if (date.isHolidays) {
        return date.lastSchoolDay
    }

    return date
}
