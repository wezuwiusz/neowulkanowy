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
            completedLessonDialogSubjectValue.text = completedLesson.subject
            completedLessonDialogTopicValue.text = completedLesson.topic
            completedLessonDialogTeacherValue.text = completedLesson.teacher
            completedLessonDialogAbsenceValue.text = completedLesson.absence
            completedLessonDialogChangesValue.text = completedLesson.substitution
            completedLessonDialogResourcesValue.text = completedLesson.resources
        }

        completedLesson.substitution.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogChangesTitle.visibility = View.GONE
                    completedLessonDialogChangesValue.visibility = View.GONE
                }
            } else binding.completedLessonDialogChangesValue.text = it
        }

        completedLesson.absence.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogAbsenceTitle.visibility = View.GONE
                    completedLessonDialogAbsenceValue.visibility = View.GONE
                }
            } else binding.completedLessonDialogAbsenceValue.text = it
        }

        completedLesson.resources.let {
            if (it.isBlank()) {
                with(binding) {
                    completedLessonDialogResourcesTitle.visibility = View.GONE
                    completedLessonDialogResourcesValue.visibility = View.GONE
                }
            } else binding.completedLessonDialogResourcesValue.text = it
        }

        binding.completedLessonDialogClose.setOnClickListener { dismiss() }
    }
}
