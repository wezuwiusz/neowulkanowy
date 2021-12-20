package io.github.wulkanowy.ui.modules.timetable.additional.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogAdditionalAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.SchoolDaysValidator
import io.github.wulkanowy.utils.lastSchoolDayInSchoolYear
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class AdditionalLessonAddDialog : BaseDialogFragment<DialogAdditionalAddBinding>(),
    AdditionalLessonAddView {

    @Inject
    lateinit var presenter: AdditionalLessonAddPresenter

    companion object {
        fun newInstance() = AdditionalLessonAddDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogAdditionalAddBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding) {
            additionalLessonDialogStartEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogStart.isErrorEnabled = false
                additionalLessonDialogStart.error = null
            }
            additionalLessonDialogEndEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogEnd.isErrorEnabled = false
                additionalLessonDialogEnd.error = null
            }
            additionalLessonDialogDateEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogDate.isErrorEnabled = false
                additionalLessonDialogDate.error = null
            }
            additionalLessonDialogContentEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogContent.isErrorEnabled = false
                additionalLessonDialogContent.error = null
            }

            additionalLessonDialogAdd.setOnClickListener {
                presenter.onAddAdditionalClicked(
                    start = additionalLessonDialogStartEdit.text?.toString(),
                    end = additionalLessonDialogEndEdit.text?.toString(),
                    date = additionalLessonDialogDateEdit.text?.toString(),
                    content = additionalLessonDialogContentEdit.text?.toString(),
                    isRepeat = additionalLessonDialogRepeat.isChecked
                )
            }
            additionalLessonDialogClose.setOnClickListener { dismiss() }
            additionalLessonDialogDateEdit.setOnClickListener { presenter.showDatePicker() }
            additionalLessonDialogStartEdit.setOnClickListener { presenter.showStartTimePicker() }
            additionalLessonDialogEndEdit.setOnClickListener { presenter.showEndTimePicker() }
        }
    }

    override fun showSuccessMessage() {
        showMessage(getString(R.string.additional_lessons_add_success))
    }

    override fun setErrorDateRequired() {
        with(binding.additionalLessonDialogDate) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorStartRequired() {
        with(binding.additionalLessonDialogStart) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorEndRequired() {
        with(binding.additionalLessonDialogEnd) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorContentRequired() {
        with(binding.additionalLessonDialogContent) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorIncorrectEndTime() {
        with(binding.additionalLessonDialogEnd) {
            isErrorEnabled = true
            error = getString(R.string.additional_lessons_end_time_error)
        }
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun showDatePickerDialog(selectedDate: LocalDate) {
        val rangeStart = LocalDate.now().toTimestamp()
        val rangeEnd = LocalDate.now().lastSchoolDayInSchoolYear.toTimestamp()
        val constraintsBuilder = CalendarConstraints.Builder().apply {
            setStart(rangeStart)
            setEnd(rangeEnd)
            setValidator(SchoolDaysValidator(rangeStart, rangeEnd))
        }
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(selectedDate.toTimestamp())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val date = it.toLocalDateTime().toLocalDate()
            presenter.onDateSelected(date)
            binding.additionalLessonDialogDateEdit.setText(date.toFormattedString())
        }

        if (!parentFragmentManager.isStateSaved) {
            datePicker.show(parentFragmentManager, null)
        }
    }

    override fun showStartTimePickerDialog(selectedTime: LocalTime) {
        showTimePickerDialog(selectedTime) {
            presenter.onStartTimeSelected(it)
            binding.additionalLessonDialogStartEdit.setText(it.toString())
        }
    }

    override fun showEndTimePickerDialog(selectedTime: LocalTime) {
        showTimePickerDialog(selectedTime) {
            presenter.onEndTimeSelected(it)
            binding.additionalLessonDialogEndEdit.setText(it.toString())
        }
    }

    private fun showTimePickerDialog(defaultTime: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(defaultTime.hour)
            .setMinute(defaultTime.minute)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            onTimeSelected(LocalTime.of(timePicker.hour, timePicker.minute))
        }

        if (!parentFragmentManager.isStateSaved) {
            timePicker.show(parentFragmentManager, null)
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
