package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.databinding.DialogGradeBinding
import io.github.wulkanowy.utils.colorStringId
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.getGradeColor
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.toFormattedString

class GradeDetailsDialog : DialogFragment() {

    private var binding: DialogGradeBinding by lifecycleAwareVariable()

    private lateinit var grade: Grade

    private lateinit var colorScheme: String

    companion object {

        private const val ARGUMENT_KEY = "Item"

        private const val COLOR_SCHEME_KEY = "Scheme"

        fun newInstance(grade: Grade, colorScheme: String) =
            GradeDetailsDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARGUMENT_KEY, grade)
                    putString(COLOR_SCHEME_KEY, colorScheme)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            grade = getSerializable(ARGUMENT_KEY) as Grade
            colorScheme = getString(COLOR_SCHEME_KEY) ?: "default"
        }
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
                setBackgroundResource(grade.getBackgroundColor(colorScheme))
            }

            gradeDialogTeacherValue.text = if (grade.teacher.isBlank()) {
                getString(R.string.all_no_data)
            } else grade.teacher

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
