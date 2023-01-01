package io.github.wulkanowy.ui.modules.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.DialogExamBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.openCalendarEventAdd
import io.github.wulkanowy.utils.serializable
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalTime

class ExamDialog : DialogFragment() {

    private var binding: DialogExamBinding by lifecycleAwareVariable()

    private lateinit var exam: Exam

    companion object {

        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Exam) = ExamDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to exam)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        exam = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogExamBinding.inflate(inflater).apply { binding = this }.root

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
