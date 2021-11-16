package io.github.wulkanowy.ui.modules.homework.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogHomeworkAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkAddDialog : BaseDialogFragment<DialogHomeworkAddBinding>(), HomeworkAddView {

    @Inject
    lateinit var presenter: HomeworkAddPresenter

    private var date: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogHomeworkAddBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding) {
            homeworkDialogSubjectEdit.doOnTextChanged { _, _, _, _ ->
                homeworkDialogSubject.error = null
                homeworkDialogSubject.isErrorEnabled = false
            }
            homeworkDialogDateEdit.doOnTextChanged { _, _, _, _ ->
                homeworkDialogDate.error = null
                homeworkDialogDate.isErrorEnabled = false
            }
            homeworkDialogContentEdit.doOnTextChanged { _, _, _, _ ->
                homeworkDialogContent.error = null
                homeworkDialogContent.isErrorEnabled = false
            }
            homeworkDialogClose.setOnClickListener { dismiss() }
            homeworkDialogDateEdit.setOnClickListener { presenter.showDatePicker(date) }
            homeworkDialogAdd.setOnClickListener {
                presenter.onAddHomeworkClicked(
                    subject = homeworkDialogSubjectEdit.text?.toString(),
                    teacher = homeworkDialogTeacherEdit.text?.toString(),
                    date = homeworkDialogDateEdit.text?.toString(),
                    content = homeworkDialogContentEdit.text?.toString()
                )
            }
        }
    }

    override fun showSuccessMessage() {
        showMessage(getString(R.string.homework_add_success))
    }

    override fun setErrorSubjectRequired() {
        with(binding.homeworkDialogSubject) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorDateRequired() {
        with(binding.homeworkDialogDate) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorContentRequired() {
        with(binding.homeworkDialogContent) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun showDatePickerDialog(currentDate: LocalDate) {
        val constraintsBuilder = CalendarConstraints.Builder().apply {
            setStart(LocalDate.now().toEpochDay())
        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(currentDate.toTimestamp())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            date = it.toLocalDateTime().toLocalDate()
            binding.homeworkDialogDate.editText?.setText(date!!.toFormattedString())
        }

        if (!parentFragmentManager.isStateSaved) {
            datePicker.show(this.parentFragmentManager, null)
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
