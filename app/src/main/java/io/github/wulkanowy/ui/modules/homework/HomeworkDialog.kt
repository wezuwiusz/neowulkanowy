package io.github.wulkanowy.ui.modules.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_homework.*

class HomeworkDialog : DialogFragment() {

    private lateinit var homework: Homework

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(homework: Homework): HomeworkDialog {
            return HomeworkDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, homework) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            homework = getSerializable(HomeworkDialog.ARGUMENT_KEY) as Homework
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_homework, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeworkDialogDate.text = homework.date.toFormattedString()
        homeworkDialogEntryDate.text = homework.entryDate.toFormattedString()
        homeworkDialogSubject.text = homework.subject
        homeworkDialogTeacher.text = homework.teacher
        homeworkDialogContent.text = homework.content
        homeworkDialogClose.setOnClickListener { dismiss() }
    }
}
