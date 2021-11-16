package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Attendance
import java.time.LocalDate

val debugAttendanceItems = listOf(
    generateAttendance("Matematyka", "PRESENCE"),
    generateAttendance("Język angielski", "UNEXCUSED_LATENESS"),
    generateAttendance("Geografia", "ABSENCE_UNEXCUSED"),
    generateAttendance("Sieci komputerowe", "ABSENCE_EXCUSED"),
    generateAttendance("Systemy operacyjne", "EXCUSED_LATENESS"),
    generateAttendance("Język niemiecki", "ABSENCE_UNEXCUSED"),
    generateAttendance("Biologia", "ABSENCE_UNEXCUSED"),
    generateAttendance("Chemia", "ABSENCE_EXCUSED"),
    generateAttendance("Fizyka", "ABSENCE_UNEXCUSED"),
    generateAttendance("Matematyka", "ABSENCE_EXCUSED"),
)

private fun generateAttendance(subject: String, name: String) = Attendance(
    subject = subject,
    studentId = 0,
    diaryId = 0,
    date = LocalDate.now(),
    timeId = 0,
    number = 1,
    name = name,
    presence = false,
    absence = false,
    exemption = false,
    lateness = false,
    excused = false,
    deleted = false,
    excusable = false,
    excuseStatus = ""
)
