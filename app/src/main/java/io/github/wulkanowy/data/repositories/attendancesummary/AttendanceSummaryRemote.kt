package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRemote @Inject constructor(private val sdk: Sdk) {

    fun getAttendanceSummary(semester: Semester, subjectId: Int): Single<List<AttendanceSummary>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getAttendanceSummary(subjectId)
            .map { attendance ->
                attendance.map {
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
}
