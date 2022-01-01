package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun Fragment.openMaterialDatePicker(
    selected: LocalDate,
    rangeStart: LocalDate,
    rangeEnd: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val constraintsBuilder = CalendarConstraints.Builder().apply {
        setValidator(CalendarDayRangeValidator(rangeStart, rangeEnd))
        setStart(rangeStart.toTimestamp())
        setEnd(rangeEnd.toTimestamp())
    }

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setCalendarConstraints(constraintsBuilder.build())
        .setSelection(selected.toTimestamp())
        .build()

    datePicker.addOnPositiveButtonClickListener {
        val date = it.toLocalDateTime().toLocalDate()
        onDateSelected(date)
    }

    if (!parentFragmentManager.isStateSaved) {
        datePicker.show(parentFragmentManager, null)
    }
}

@Parcelize
private class CalendarDayRangeValidator(
    val start: LocalDate,
    val end: LocalDate,
) : CalendarConstraints.DateValidator {

    override fun isValid(dateLong: Long): Boolean {
        val date = dateLong.toLocalDateTime().toLocalDate()
        val daysUntilEnd = date.until(end, ChronoUnit.DAYS)
        val daysUntilStart = date.until(start, ChronoUnit.DAYS)

        return daysUntilStart <= 0 && daysUntilEnd >= 0
    }
}
