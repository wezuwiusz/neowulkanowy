package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.utils.toDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalDateTime.now
import io.github.wulkanowy.api.timetable.Timetable as TimetableRemote
import io.github.wulkanowy.data.db.entities.Timetable as TimetableLocal

fun createTimetableLocal(number: Int, start: LocalDateTime, room: String = "", subject: String = "", teacher: String = "", changes: Boolean = false): TimetableLocal {
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
        changes = changes,
        canceled = false
    )
}

fun createTimetableRemote(number: Int, start: LocalDateTime, room: String, subject: String = "", teacher: String = "", changes: Boolean = false): TimetableRemote {
    return TimetableRemote(
        number = number,
        start = start.toDate(),
        end = start.plusMinutes(45).toDate(),
        date = start.toLocalDate().toDate(),
        subject = subject,
        group = "",
        room = room,
        teacher = teacher,
        info = "",
        changes = changes,
        canceled = false
    )
}
