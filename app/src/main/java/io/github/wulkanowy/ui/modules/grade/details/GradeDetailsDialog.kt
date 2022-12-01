package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.enums.GradeColorTheme
import io.github.wulkanowy.databinding.DialogGradeBinding
import io.github.wulkanowy.utils.*


class GradeDetailsDialog : DialogFragment() {

    private var binding: DialogGradeBinding by lifecycleAwareVariable()

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
        setStyle(STYLE_NO_TITLE, 0)
        grade = requireArguments().serializable(ARGUMENT_KEY)
        gradeColorTheme = requireArguments().serializable(COLOR_THEME_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogGradeBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            gradeDialogSubject.text = grade.subject

            gradeDialogColorAndWeightValue.run {
                text = context.getString(R.string.grade_weight_value, grade.weight)
                setBackgroundResource(grade.getGradeColor())
            }

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
                setBackgroundResource(grade.getBackgroundColor(gradeColorTheme))
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
