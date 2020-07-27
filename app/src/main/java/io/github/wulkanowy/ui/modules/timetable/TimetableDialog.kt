package io.github.wulkanowy.ui.modules.timetable

import android.annotation.SuppressLint
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.databinding.DialogTimetableBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDateTime

class TimetableDialog : DialogFragment() {

    private var binding: DialogTimetableBinding by lifecycleAwareVariable()

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
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            lesson = getSerializable(ARGUMENT_KEY) as Timetable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogTimetableBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(lesson) {
            setInfo(info, teacher, canceled, changes)
            setSubject(subject, subjectOld)
            setTeacher(teacher, teacherOld)
            setGroup(group)
            setRoom(room, roomOld)
            setTime(start, end)
        }

        binding.timetableDialogClose.setOnClickListener { dismiss() }
    }

    private fun setSubject(subject: String, subjectOld: String) {
        with(binding) {
            timetableDialogSubject.text = subject
            if (subjectOld.isNotBlank() && subjectOld != subject) {
                timetableDialogSubject.run {
                    paintFlags = paintFlags or STRIKE_THRU_TEXT_FLAG
                    text = subjectOld
                }
                timetableDialogSubjectNew.run {
                    visibility = VISIBLE
                    text = subject
                }
            }
        }
    }

    private fun setInfo(info: String, teacher: String, canceled: Boolean, changes: Boolean) {
        with(binding) {
            when {
                info.isNotBlank() -> {
                    if (canceled) {
                        timetableDialogChangesTitle.setTextColor(requireContext().getThemeAttrColor(R.attr.colorPrimary))
                        timetableDialogChanges.setTextColor(requireContext().getThemeAttrColor(R.attr.colorPrimary))
                    } else {
                        timetableDialogChangesTitle.setTextColor(requireContext().getThemeAttrColor(R.attr.colorTimetableChange))
                        timetableDialogChanges.setTextColor(requireContext().getThemeAttrColor(R.attr.colorTimetableChange))
                    }

                    timetableDialogChanges.text = when {
                        canceled && !changes -> "Lekcja odwołana: $info"
                        changes && teacher.isNotBlank() -> "Zastępstwo: $teacher"
                        changes && teacher.isBlank() -> "Zastępstwo, ${info.decapitalize()}"
                        else -> info.capitalize()
                    }
                }
                else -> {
                    timetableDialogChangesTitle.visibility = GONE
                    timetableDialogChanges.visibility = GONE
                }
            }
        }
    }

    private fun setTeacher(teacher: String, teacherOld: String) {
        with(binding) {
            when {
                teacherOld.isNotBlank() && teacherOld != teacher -> {
                    timetableDialogTeacher.run {
                        visibility = VISIBLE
                        paintFlags = paintFlags or STRIKE_THRU_TEXT_FLAG
                        text = teacherOld
                    }
                    if (teacher.isNotBlank()) {
                        timetableDialogTeacherNew.run {
                            visibility = VISIBLE
                            text = teacher
                        }
                    }
                }
                teacher.isNotBlank() -> timetableDialogTeacher.text = teacher
                else -> {
                    timetableDialogTeacherTitle.visibility = GONE
                    timetableDialogTeacher.visibility = GONE
                }
            }
        }
    }

    private fun setGroup(group: String) {
        with(binding) {
            when {
                group.isNotBlank() -> timetableDialogGroup.text = group
                else -> {
                    timetableDialogGroupTitle.visibility = GONE
                    timetableDialogGroup.visibility = GONE
                }
            }
        }
    }

    private fun setRoom(room: String, roomOld: String) {
        with(binding) {
            when {
                roomOld.isNotBlank() && roomOld != room -> {
                    timetableDialogRoom.run {
                        visibility = VISIBLE
                        paintFlags = paintFlags or STRIKE_THRU_TEXT_FLAG
                        text = roomOld
                    }
                    if (room.isNotBlank()) {
                        timetableDialogRoomNew.run {
                            visibility = VISIBLE
                            text = room
                        }
                    }
                }
                room.isNotBlank() -> timetableDialogRoom.text = room
                else -> {
                    timetableDialogRoomTitle.visibility = GONE
                    timetableDialogRoom.visibility = GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTime(start: LocalDateTime, end: LocalDateTime) {
        binding.timetableDialogTime.text = "${start.toFormattedString("HH:mm")} - ${end.toFormattedString("HH:mm")}"
    }
}
