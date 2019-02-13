package io.github.wulkanowy.ui.modules.timetable.completed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.CompletedLesson
import kotlinx.android.synthetic.main.dialog_lesson_completed.*

class CompletedLessonDialog : DialogFragment() {

    private lateinit var completedLesson: CompletedLesson

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: CompletedLesson): CompletedLessonDialog {
            return CompletedLessonDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            completedLesson = getSerializable(CompletedLessonDialog.ARGUMENT_KEY) as CompletedLesson
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_lesson_completed, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        completedLessonDialogSubject.text = completedLesson.subject
        completedLessonDialogTopic.text = completedLesson.topic
        completedLessonDialogTeacher.text = completedLesson.teacher
        completedLessonDialogAbsence.text = completedLesson.absence
        completedLessonDialogChanges.text = completedLesson.substitution
        completedLessonDialogResources.text = completedLesson.resources

        completedLesson.substitution.let {
            if (it.isBlank()) {
                completedLessonDialogChangesTitle.visibility = View.GONE
                completedLessonDialogChanges.visibility = View.GONE
            } else completedLessonDialogChanges.text = it
        }

        completedLesson.absence.let {
            if (it.isBlank()) {
                completedLessonDialogAbsenceTitle.visibility = View.GONE
                completedLessonDialogAbsence.visibility = View.GONE
            } else completedLessonDialogAbsence.text = it
        }

        completedLesson.resources.let {
            if (it.isBlank()) {
                completedLessonDialogResourcesTitle.visibility = View.GONE
                completedLessonDialogResources.visibility = View.GONE
            } else completedLessonDialogResources.text = it
        }

        completedLessonDialogClose.setOnClickListener { dismiss() }
    }
}
