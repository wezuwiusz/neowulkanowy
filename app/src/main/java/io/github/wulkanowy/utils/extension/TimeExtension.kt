package io.github.wulkanowy.utils.extension

import io.github.wulkanowy.utils.DATE_PATTERN
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters
import java.text.SimpleDateFormat
import java.util.*

fun Date.toLocalDate(): LocalDate = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this))

fun String.toDate(format: String = "yyyy-MM-dd"): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern(format))

fun LocalDate.toFormat(format: String): String = this.format(DateTimeFormatter.ofPattern(format))

fun LocalDate.toFormat(): String = this.toFormat(DATE_PATTERN)

fun LocalDate.getNextWorkDay(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        else -> this.plusDays(1)
    }
}

fun LocalDate.getPreviousWorkDay(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY -> this.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY))
        else -> this.minusDays(1)
    }
}

fun LocalDate.getNearSchoolDayPrevOnWeekEnd(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> this.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY))
        else -> this
    }
}

fun LocalDate.getNearSchoolDayNextOnWeekEnd(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        else -> this
    }
}

fun LocalDate.getWeekDayName(): String = this.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))

fun LocalDate.getWeekFirstDayAlwaysCurrent(): LocalDate {
    return this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun LocalDate.getWeekFirstDayNextOnWeekEnd(): LocalDate {
    return when(this.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        else -> this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
}

/**
 * [Dz.U. 2016 poz. 1335](http://prawo.sejm.gov.pl/isap.nsf/DocDetails.xsp?id=WDU20160001335)
 */
fun LocalDate.isHolidays(): Boolean = this.isAfter(this.getLastSchoolDay()) && this.isBefore(this.getFirstSchoolDay())

fun LocalDate.getSchoolYear(): Int = if (this.monthValue <= 8) this.year - 1 else this.year

fun LocalDate.getFirstSchoolDay(): LocalDate {
    return LocalDate.of(this.year, 9, 1).run {
        when (dayOfWeek) {
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
            else -> this
        }
    }
}

fun LocalDate.getLastSchoolDay(): LocalDate {
    return LocalDate
            .of(this.year, 6, 20)
            .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
}
