package io.github.wulkanowy.ui.modules.timetable.completed

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.databinding.DialogLessonCompletedBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.serializable

@AndroidEntryPoint
class CompletedLessonDialog : BaseDialogFragment<DialogLessonCompletedBinding>() {

    private lateinit var completedLesson: CompletedLesson

    companion object {

        private const val ARGUMENT_KEY = "Item"

        fun newInstance(lesson: CompletedLesson) = CompletedLessonDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to lesson)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        completedLesson = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogLessonCompletedBinding.inflate(layoutInflater).apply { binding = this }.root
            )
            .create()
    }

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
