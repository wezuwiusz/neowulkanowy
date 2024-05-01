package io.github.wulkanowy.ui.modules.timetable.additional.add

import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate
import java.time.LocalTime

interface AdditionalLessonAddView : BaseView {

    fun initView(selectedDate: LocalDate)

    fun closeDialog()

    fun showDatePickerDialog(selectedDate: LocalDate)

    fun showStartTimePickerDialog(selectedTime: LocalTime)

    fun showEndTimePickerDialog(selectedTime: LocalTime)

    fun showSuccessMessage()

    fun setErrorDateRequired()

    fun setErrorStartRequired()

    fun setErrorEndRequired()

    fun setErrorContentRequired()

    fun setErrorIncorrectEndTime()
}
