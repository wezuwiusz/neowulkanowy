package io.github.wulkanowy.ui.main.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.dialog_attendance.*

class AttendanceDialog : DialogFragment() {

    private lateinit var attendance: Attendance

    companion object {
        private const val ARGUMENT_KEY = "Item"

        fun newInstance(exam: Attendance): AttendanceDialog {
            return AttendanceDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, exam) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            attendance = getSerializable(ARGUMENT_KEY) as Attendance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_attendance, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        attendanceDialogSubject.text = attendance.subject
        attendanceDialogDescription.text = attendance.name
        attendanceDialogDate.text = attendance.date.toFormattedString()
        attendanceDialogNumber.text = attendance.number.toString()
        attendanceDialogClose.setOnClickListener { dismiss() }
    }
}
