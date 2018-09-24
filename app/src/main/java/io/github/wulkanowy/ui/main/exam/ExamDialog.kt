package io.github.wulkanowy.ui.main.exam

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.utils.extension.toFormat
import kotlinx.android.synthetic.main.dialog_exam.*

class ExamDialog : DialogFragment() {

    private lateinit var exam: Exam

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Exam): ExamDialog {
            return ExamDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme)
        arguments?.run {
            exam = getSerializable(ARGUMENT_KEY) as Exam
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(getString(R.string.all_details))
        return inflater.inflate(R.layout.dialog_exam, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        examDialogSubjectValue.text = exam.subject
        examDialogTypeValue.text = exam.type
        examDialogTeacherValue.text = exam.teacher
        examDialogDateValue.text = exam.entryDate.toFormat()
        examDialogDescriptionValue.text = exam.description

        examDialogClose.setOnClickListener { dismiss() }
    }
}
