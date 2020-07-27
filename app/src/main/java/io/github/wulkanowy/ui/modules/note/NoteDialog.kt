package io.github.wulkanowy.ui.modules.note

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.databinding.DialogNoteBinding
import io.github.wulkanowy.sdk.scrapper.notes.Note.CategoryType
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.toFormattedString

class NoteDialog : DialogFragment() {

    private var binding: DialogNoteBinding by lifecycleAwareVariable()

    private lateinit var note: Note

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Note): NoteDialog {
            return NoteDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            note = getSerializable(ARGUMENT_KEY) as Note
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogNoteBinding.inflate(inflater).apply { binding = this }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(binding) {
            noteDialogDate.text = note.date.toFormattedString()
            noteDialogCategory.text = note.category
            noteDialogTeacher.text = note.teacher
            noteDialogContent.text = note.content
        }

        if (note.isPointsShow) {
            with(binding.noteDialogPoints) {
                text = "${if (note.points > 0) "+" else ""}${note.points}"
                setTextColor(when (CategoryType.getByValue(note.categoryType)) {
                    CategoryType.POSITIVE -> ContextCompat.getColor(requireContext(), R.color.note_positive)
                    CategoryType.NEGATIVE -> ContextCompat.getColor(requireContext(), R.color.note_negative)
                    else -> requireContext().getThemeAttrColor(android.R.attr.textColorPrimary)
                })
            }
        }

        binding.noteDialogClose.setOnClickListener { dismiss() }
    }
}
