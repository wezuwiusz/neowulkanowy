package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.Attendance as SdkAttendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary as SdkAttendanceSummary

fun List<SdkAttendance>.mapToEntities(semester: Semester) = map {
    Attendance(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        date = it.date,
        timeId = it.timeId,
        number = it.number,
        subject = it.subject,
        name = it.name,
        presence = it.presence,
        absence = it.absence,
        exemption = it.exemption,
        lateness = it.lateness,
        excused = it.excused,
        deleted = it.deleted,
        excusable = it.excusable,
        excuseStatus = it.excuseStatus?.name
    )
}

fun List<SdkAttendanceSummary>.mapToEntities(semester: Semester, subjectId: Int) = map {
    AttendanceSummary(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        subjectId = subjectId,
        month = it.month,
        presence = it.presence,
        absence = it.absence,
        absenceExcused = it.absenceExcused,
        absenceForSchoolReasons = it.absenceForSchoolReasons,
        lateness = it.lateness,
        latenessExcused = it.latenessExcused,
        exemption = it.exemption
    )
}
