package io.github.wulkanowy.utils

import org.threeten.bp.DayOfWeek.FRIDAY
import org.threeten.bp.DayOfWeek.MONDAY
import org.threeten.bp.DayOfWeek.SATURDAY
import org.threeten.bp.DayOfWeek.SUNDAY
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.format.TextStyle.FULL_STANDALONE
import org.threeten.bp.temporal.TemporalAdjusters.firstInMonth
import org.threeten.bp.temporal.TemporalAdjusters.next
import org.threeten.bp.temporal.TemporalAdjusters.previous
import java.util.Date
import java.util.Locale

private const val DATE_PATTERN = "dd.MM.yyyy"

fun Date.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Date.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun String.toLocalDate(format: String = DATE_PATTERN): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
}

fun LocalDate.toFormattedString(format: String = DATE_PATTERN): String = this.format(ofPattern(format))

fun LocalDateTime.toFormattedString(format: String = DATE_PATTERN): String = this.format(ofPattern(format))

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
        return when (this.dayOfWeek) {
            FRIDAY, SATURDAY, SUNDAY -> this.with(next(MONDAY))
            else -> this.plusDays(1)
        }
    }

inline val LocalDate.previousSchoolDay: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY, MONDAY -> this.with(previous(FRIDAY))
            else -> this.minusDays(1)
        }
    }

inline val LocalDate.nextOrSameSchoolDay: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY -> this.with(next(MONDAY))
            else -> this
        }
    }

inline val LocalDate.previousOrSameSchoolDay: LocalDate
    get() {
        return when (this.dayOfWeek) {
            SATURDAY, SUNDAY -> this.with(previous(FRIDAY))
            else -> this
        }
    }

inline val LocalDate.weekDayName: String
    get() = this.format(ofPattern("EEEE", Locale.getDefault()))

inline val LocalDate.monday: LocalDate
    get() = this.with(MONDAY)

inline val LocalDate.friday: LocalDate
    get() = this.with(FRIDAY)

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
