package io.github.wulkanowy.ui.modules.timetable.additional.add

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogAdditionalAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.lastSchoolDayInSchoolYear
import io.github.wulkanowy.utils.openMaterialDatePicker
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class AdditionalLessonAddDialog : BaseDialogFragment<DialogAdditionalAddBinding>(),
    AdditionalLessonAddView {

    @Inject
    lateinit var presenter: AdditionalLessonAddPresenter

    companion object {
        const val ARGUMENT_KEY = "additional_lesson_default_date"
        fun newInstance(defaultDate: LocalDate) = AdditionalLessonAddDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to defaultDate.toEpochDay())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogAdditionalAddBinding.inflate(layoutInflater).apply { binding = this }.root
            )
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getLong(ARGUMENT_KEY)?.let(LocalDate::ofEpochDay)?.let {
            presenter.onDateSelected(it)
        }
        presenter.onAttachView(this)
    }

    override fun initView(selectedDate: LocalDate) {
        with(binding) {
            additionalLessonDialogStartEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogStart.isErrorEnabled = false
                additionalLessonDialogStart.error = null
            }
            additionalLessonDialogEndEdit.doOnTextChanged { _, _, _, _ ->
                additionalLessonDialogEnd.isErrorEnabled = false
                additionalLessonDialogEnd.error = null
            }
            additionalLessonDialogDateEdit.setText(selectedDate.toFormattedString())
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
        openMaterialDatePicker(
            selected = selectedDate,
            rangeStart = LocalDate.now(),
            rangeEnd = LocalDate.now().lastSchoolDayInSchoolYear,
            onDateSelected = {
                presenter.onDateSelected(it)
                binding.additionalLessonDialogDateEdit.setText(it.toFormattedString())
            }
        )
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
            if (isAdded) {
                onTimeSelected(LocalTime.of(timePicker.hour, timePicker.minute))
            }
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
