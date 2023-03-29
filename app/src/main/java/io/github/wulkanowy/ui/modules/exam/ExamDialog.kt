package io.github.wulkanowy.ui.modules.exam

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.DialogExamBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.openCalendarEventAdd
import io.github.wulkanowy.utils.serializable
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalTime

@AndroidEntryPoint
class ExamDialog : BaseDialogFragment<DialogExamBinding>() {

    private lateinit var exam: Exam

    companion object {

        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Exam) = ExamDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to exam)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exam = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(DialogExamBinding.inflate(layoutInflater).apply { binding = this }.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            examDialogSubjectValue.text = exam.subject
            examDialogTypeValue.text = exam.type
            examDialogTeacherValue.text = exam.teacher
            examDialogEntryDateValue.text = exam.entryDate.toFormattedString()
            examDialogDeadlineDateValue.text = exam.date.toFormattedString()
            examDialogDescriptionValue.text = exam.description.ifBlank {
                getString(R.string.all_no_data)
            }

            examDialogClose.setOnClickListener { dismiss() }
            examDialogAddToCalendar.setOnClickListener {
                requireContext().openCalendarEventAdd(
                    title = "${exam.subject} - ${exam.type}",
                    description = exam.description,
                    start = exam.date.atTime(LocalTime.of(8, 0)),
                    end = exam.date.atTime(LocalTime.of(8, 45)),
                )
            }
        }
    }
}
