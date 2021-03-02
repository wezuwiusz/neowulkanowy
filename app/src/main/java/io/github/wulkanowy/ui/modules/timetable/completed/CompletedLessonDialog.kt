package io.github.wulkanowy.ui.modules.timetable.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.databinding.DialogLessonCompletedBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable

class CompletedLessonDialog : DialogFragment() {

    private var binding: DialogLessonCompletedBinding by lifecycleAwareVariable()

    private lateinit var completedLesson: CompletedLesson

    companion object {

        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: CompletedLesson) = CompletedLessonDialog().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            completedLesson = getSerializable(ARGUMENT_KEY) as CompletedLesson
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogLessonCompletedBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            completedLessonDialogSubject.text = completedLesson.subject
            completedLessonDialogTopic.text = completedLesson.topic
            completedLessonDialogTeacher.text = completedLesson.teacher
            completedLessonDialogAbsence.text = completedLesson.absence
            completedLessonDialogChanges.text = completedLesson.substitution
            completedLessonDialogResources.text = completedLesson.resources
        }

        completedLesson.substitution.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogChangesTitle.visibility = View.GONE
                    completedLessonDialogChanges.visibility = View.GONE
                }
            } else binding.completedLessonDialogChanges.text = it
        }

        completedLesson.absence.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogAbsenceTitle.visibility = View.GONE
                    completedLessonDialogAbsence.visibility = View.GONE
                }
            } else binding.completedLessonDialogAbsence.text = it
        }

        completedLesson.resources.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogResourcesTitle.visibility = View.GONE
                    completedLessonDialogResources.visibility = View.GONE
                }
            } else binding.completedLessonDialogResources.text = it
        }

        binding.completedLessonDialogClose.setOnClickListener { dismiss() }
    }
}
