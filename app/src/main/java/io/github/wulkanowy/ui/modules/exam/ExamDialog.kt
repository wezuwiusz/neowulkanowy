package io.github.wulkanowy.ui.modules.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.DialogExamBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.toFormattedString

class ExamDialog : DialogFragment() {

    private var binding: DialogExamBinding by lifecycleAwareVariable()

    private lateinit var exam: Exam

    companion object {

        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Exam) = ExamDialog().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            exam = getSerializable(ARGUMENT_KEY) as Exam
        }
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
            examDialogDateValue.text = exam.entryDate.toFormattedString()
            examDialogDescriptionValue.text = exam.description

            examDialogClose.setOnClickListener { dismiss() }
        }
    }
}
