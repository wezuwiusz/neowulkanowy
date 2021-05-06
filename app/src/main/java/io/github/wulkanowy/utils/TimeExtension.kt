package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.ofInstant
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ofPattern
import java.time.temporal.TemporalAdjusters.firstInMonth
import java.time.temporal.TemporalAdjusters.next
import java.time.temporal.TemporalAdjusters.previous
import java.util.Locale

private const val DATE_PATTERN = "dd.MM.yyyy"

fun String.toLocalDate(format: String = DATE_PATTERN): LocalDate =
    LocalDate.parse(this, ofPattern(format))

fun LocalDateTime.toTimestamp() =
    atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDateTime(): LocalDateTime = ofInstant(ofEpochMilli(this), ZoneId.systemDefault())

fun LocalDate.toTimestamp() = atTime(LocalTime.now()).toTimestamp()

fun LocalDate.toFormattedString(format: String = DATE_PATTERN): String = format(ofPattern(format))

fun LocalDateTime.toFormattedString(format: String = DATE_PATTERN): String =
    format(ofPattern(format))

@SuppressLint("DefaultLocale")
fun Month.getFormattedName(): String {
    val formatter = SimpleDateFormat("LLLL", Locale.getDefault())

    val date = now().withMonth(value)
    return formatter.format(date.toInstant(ZoneOffset.UTC).toEpochMilli()).capitalise()
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

inline val LocalDate.startExamsDay: LocalDate
    get() = nextOrSameSchoolDay.monday

inline val LocalDate.endExamsDay: LocalDate
    get() = nextOrSameSchoolDay.monday.plusWeeks(4).minusDays(1)

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

inline val LocalDate.sunday: LocalDate
    get() = with(SUNDAY)

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

inline val LocalDate.schoolYearStart: LocalDate
    get() = withYear(if (this.monthValue <= 6) this.year - 1 else this.year).firstSchoolDay

inline val LocalDate.schoolYearEnd: LocalDate
    get() = withYear(if (this.monthValue > 6) this.year + 1 else this.year).lastSchoolDay

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
