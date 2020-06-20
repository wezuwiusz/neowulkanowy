package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int): List<AttendanceSummary> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getAttendanceSummary(subjectId)
            .map {
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
    }
}
