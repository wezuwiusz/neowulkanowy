package io.github.wulkanowy.ui.modules.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.databinding.DialogAttendanceBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.toFormattedString

class AttendanceDialog : DialogFragment() {

    private var binding: DialogAttendanceBinding by lifecycleAwareVariable()

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
        return DialogAttendanceBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(binding) {
            attendanceDialogSubject.text = attendance.subject
            attendanceDialogDescription.text = attendance.name
            attendanceDialogDate.text = attendance.date.toFormattedString()
            attendanceDialogNumber.text = attendance.number.toString()
            attendanceDialogClose.setOnClickListener { dismiss() }
        }
    }
}
