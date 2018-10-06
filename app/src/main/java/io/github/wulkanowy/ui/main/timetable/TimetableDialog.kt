package io.github.wulkanowy.ui.main.timetable

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_timetable.*

class TimetableDialog : DialogFragment() {

    private lateinit var lesson: Timetable

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Timetable): TimetableDialog {
            return TimetableDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme)
        arguments?.run {
            lesson = getSerializable(ARGUMENT_KEY) as Timetable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(getString(R.string.all_details))
        return inflater.inflate(R.layout.dialog_timetable, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timetableDialogSubject.text = lesson.subject
        timetableDialogTime.text = "${lesson.start.toFormattedString("HH:mm")} - ${lesson.end.toFormattedString("HH:mm")}"

        lesson.group.let {
            if (it.isBlank()) {
                timetableDialogGroupTitle.visibility = GONE
                timetableDialogGroup.visibility = GONE
            } else timetableDialogGroup.text = it
        }

        lesson.room.let {
            if (it.isBlank()) {
                timetableDialogRoomTitle.visibility = GONE
                timetableDialogRoom.visibility = GONE
            } else timetableDialogRoom.text = it
        }

        lesson.teacher.let {
            if (it.isBlank()) {
                timetableDialogTeacherTitle.visibility = GONE
                timetableDialogTeacher.visibility = GONE
            } else timetableDialogTeacher.text = it
        }

        lesson.info.let {
            if (it.isBlank()) {
                timetableDialogChangesTitle.visibility = GONE
                timetableDialogChanges.visibility = GONE
            } else timetableDialogChanges.text = it
        }

        timetableDialogClose.setOnClickListener { dismiss() }
    }
}
