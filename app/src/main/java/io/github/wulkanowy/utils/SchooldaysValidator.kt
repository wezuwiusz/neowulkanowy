package io.github.wulkanowy.utils

import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

@Parcelize
class SchoolDaysValidator(val start: Long, val end: Long) : CalendarConstraints.DateValidator {

    override fun isValid(dateLong: Long): Boolean {
        val date = dateLong.toLocalDateTime()

        return date.until(end.toLocalDateTime(), ChronoUnit.DAYS) >= 0 &&
            date.until(start.toLocalDateTime(), ChronoUnit.DAYS) <= 0 &&
            date.dayOfWeek != DayOfWeek.SUNDAY
    }
}