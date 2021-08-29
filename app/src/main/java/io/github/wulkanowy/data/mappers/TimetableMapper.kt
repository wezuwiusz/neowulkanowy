package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.db.entities.TimetableHeader
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.sdk.pojo.TimetableFull as SdkTimetableFull
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader as SdkTimetableHeader
import io.github.wulkanowy.sdk.pojo.Timetable as SdkTimetable
import io.github.wulkanowy.sdk.pojo.TimetableAdditional as SdkTimetableAdditional

fun SdkTimetableFull.mapToEntities(semester: Semester) = TimetableFull(
    lessons = lessons.mapToEntities(semester),
    additional = additional.mapToEntities(semester),
    headers = headers.mapToEntities(semester)
)

fun List<SdkTimetable>.mapToEntities(semester: Semester) = map {
    Timetable(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        number = it.number,
        start = it.start,
        end = it.end,
        date = it.date,
        subject = it.subject,
        subjectOld = it.subjectOld,
        group = it.group,
        room = it.room,
        roomOld = it.roomOld,
        teacher = it.teacher,
        teacherOld = it.teacherOld,
        info = it.info,
        isStudentPlan = it.studentPlan,
        changes = it.changes,
        canceled = it.canceled
    )
}

@JvmName("mapToEntitiesTimetableAdditional")
fun List<SdkTimetableAdditional>.mapToEntities(semester: Semester) = map {
    TimetableAdditional(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        subject = it.subject,
        date = it.date,
        start = it.start,
        end = it.end
    )
}

@JvmName("mapToEntitiesTimetableHeaders")
fun List<SdkTimetableHeader>.mapToEntities(semester: Semester) = map {
    TimetableHeader(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        date = it.date,
        content = it.content
    )
}
