package io.github.wulkanowy.data.repositories.timetable

import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalDateTime.now
import io.github.wulkanowy.data.db.entities.Timetable as TimetableLocal
import io.github.wulkanowy.sdk.pojo.Timetable as TimetableRemote

fun createTimetableLocal(start: LocalDateTime, number: Int, room: String = "", subject: String = "", teacher: String = "", changes: Boolean = false): TimetableLocal {
    return TimetableLocal(
        studentId = 1,
        diaryId = 2,
        number = number,
        start = start,
        end = now(),
        date = start.toLocalDate(),
        subject = subject,
        subjectOld = "",
        group = "",
        room = room,
        roomOld = "",
        teacher = teacher,
        teacherOld = "",
        info = "",
        studentPlan = true,
        changes = changes,
        canceled = false
    )
}

fun createTimetableRemote(start: LocalDateTime, number: Int = 1, room: String = "", subject: String = "", teacher: String = "", changes: Boolean = false): TimetableRemote {
    return TimetableRemote(
        number = number,
        start = start,
        end = start.plusMinutes(45),
        date = start.toLocalDate(),
        subject = subject,
        group = "",
        room = room,
        teacher = teacher,
        info = "",
        changes = changes,
        canceled = false,
        roomOld = "",
        subjectOld = "",
        teacherOld = "",
        studentPlan = true
    )
}
