package io.github.wulkanowy.ui.modules.grade.details

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.enums.GradeColorTheme
import io.github.wulkanowy.databinding.DialogGradeBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.*

@AndroidEntryPoint
class GradeDetailsDialog : BaseDialogFragment<DialogGradeBinding>() {

    private lateinit var grade: Grade

    private lateinit var gradeColorTheme: GradeColorTheme

    companion object {

        private const val ARGUMENT_KEY = "Item"

        private const val COLOR_THEME_KEY = "Theme"

        fun newInstance(grade: Grade, colorTheme: GradeColorTheme) = GradeDetailsDialog().apply {
            arguments = bundleOf(
                ARGUMENT_KEY to grade,
                COLOR_THEME_KEY to colorTheme
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        grade = requireArguments().serializable(ARGUMENT_KEY)
        gradeColorTheme = requireArguments().serializable(COLOR_THEME_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(DialogGradeBinding.inflate(layoutInflater).apply { binding = this }.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            gradeDialogSubject.text = grade.subject

            gradeDialogWeightValue.text = grade.weight
            gradeDialogWeightLayout.backgroundTintList =
                ColorStateList.valueOf(requireContext().getCompatColor(grade.getGradeColor()))

            gradeDialogDateValue.text = grade.date.toFormattedString()
            gradeDialogColorValue.text = getString(grade.colorStringId)

            gradeDialogCommentValue.apply {
                if (grade.comment.isBlank()) {
                    visibility = GONE
                    gradeDialogComment.visibility = GONE
                } else text = grade.comment
            }

            gradeDialogValue.run {
                text = grade.entry
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        grade.getBackgroundColor(gradeColorTheme)
                    )
                )
            }

            gradeDialogTeacherValue.text = grade.teacher.ifBlank { getString(R.string.all_no_data) }

            gradeDialogDescriptionValue.text = grade.run {
                when {
                    description.isBlank() && gradeSymbol.isNotBlank() -> gradeSymbol
                    description.isBlank() && gradeSymbol.isBlank() -> getString(R.string.all_no_description)
                    gradeSymbol.isNotBlank() && description.isNotBlank() -> "$gradeSymbol - $description"
                    else -> description
                }
            }

            gradeDialogClose.setOnClickListener { dismiss() }
        }
    }
}
