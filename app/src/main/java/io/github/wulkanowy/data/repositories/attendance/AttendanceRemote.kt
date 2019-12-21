package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRemote @Inject constructor(private val sdk: Sdk) {

    fun getAttendance(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Attendance>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getAttendance(startDate, endDate, semester.semesterId)
            .map { attendance ->
                attendance.map {
                    Attendance(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        date = it.date,
                        number = it.number,
                        subject = it.subject,
                        name = it.name,
                        presence = it.presence,
                        absence = it.absence,
                        exemption = it.exemption,
                        lateness = it.lateness,
                        excused = it.excused,
                        deleted = it.deleted
                    )
                }
            }
    }
}
