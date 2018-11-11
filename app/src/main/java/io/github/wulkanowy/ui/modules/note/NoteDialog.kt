package io.github.wulkanowy.ui.modules.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_note.*

class NoteDialog : DialogFragment() {

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
        return inflater.inflate(R.layout.dialog_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        noteDialogDate.text = note.date.toFormattedString()
        noteDialogCategory.text = note.category
        noteDialogTeacher.text = note.teacher
        noteDialogContent.text = note.content
        noteDialogClose.setOnClickListener { dismiss() }
    }
}
