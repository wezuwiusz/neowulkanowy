package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.utils.colorStringId
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_grade.*

class GradeDetailsDialog : DialogFragment() {

    private lateinit var grade: Grade

    private lateinit var colorScheme: String

    companion object {
        private const val ARGUMENT_KEY = "Item"
        private const val COLOR_SCHEME_KEY = "Scheme"

        fun newInstance(grade: Grade, colorScheme: String): GradeDetailsDialog {
            return GradeDetailsDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARGUMENT_KEY, grade)
                    putString(COLOR_SCHEME_KEY, colorScheme)
                }
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_grade, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        gradeDialogSubject.text = grade.subject
        gradeDialogWeightValue.text = grade.weight
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
