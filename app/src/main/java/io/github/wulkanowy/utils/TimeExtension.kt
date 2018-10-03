package io.github.wulkanowy.utils

import org.threeten.bp.DayOfWeek.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.temporal.TemporalAdjusters
import org.threeten.bp.temporal.TemporalAdjusters.*
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_PATTERN = "yyyy-MM-dd"

fun Date.toLocalDate(): LocalDate {
    return LocalDate.parse(SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(this))
}

fun String.toLocalDate(format: String = DATE_PATTERN): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
}

fun LocalDate.toFormattedString(format: String): String = this.format(ofPattern(format))

fun LocalDate.toFormattedString(): String = this.toFormattedString(DATE_PATTERN)

inline val LocalDate.nextWorkDay: LocalDate
    get() {
        return when (this.dayOfWeek) {
            FRIDAY, SATURDAY, SUNDAY -> this.with(next(MONDAY))
            else -> this.plusDays(1)
        }
    }

inline val LocalDate.previousWorkDay: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY, MONDAY -> this.with(previous(FRIDAY))
            else -> this.minusDays(1)
        }
    }

inline val LocalDate.nearSchoolDayPrevOnWeekEnd: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY -> this.with(previous(FRIDAY))
            else -> this
        }
    }

inline val LocalDate.nearSchoolDayNextOnWeekEnd: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY -> this.with(next(MONDAY))
            else -> this
        }
    }

inline val LocalDate.weekDayName: String
    get() = this.format(ofPattern("EEEE", Locale.getDefault()))

inline val LocalDate.weekFirstDayAlwaysCurrent: LocalDate
    get() = this.with(TemporalAdjusters.previousOrSame(MONDAY))

inline val LocalDate.weekFirstDayNextOnWeekEnd: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY -> this.with(next(MONDAY))
            else -> this.with(previousOrSame(MONDAY))
        }
    }

/**
 * [Dz.U. 2016 poz. 1335](http://prawo.sejm.gov.pl/isap.nsf/DocDetails.xsp?id=WDU20160001335)
 */
inline val LocalDate.isHolidays: Boolean
    get() {
        return LocalDate.of(this.year, 9, 1).run {
            when (dayOfWeek) {
                FRIDAY, SATURDAY, SUNDAY -> with(firstInMonth(MONDAY))
                else -> this
            }
        }.let { firstSchoolDay ->
            LocalDate.of(this.year, 6, 20)
                    .with(next(FRIDAY))
                    .let { lastSchoolDay -> this.isBefore(firstSchoolDay) && this.isAfter(lastSchoolDay) }
        }
    }
