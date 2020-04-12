package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRemote @Inject constructor(private val sdk: Sdk) {

    fun getAttendance(student: Student, semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Attendance>> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getAttendance(startDate, endDate, semester.semesterId)
            .map { attendance ->
                attendance.map {
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
            }
    }

    fun excuseAbsence(student: Student, semester: Semester, absenceList: List<Attendance>, reason: String?): Single<Boolean> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear).excuseForAbsence(absenceList.map { attendance ->
            Absent(
                date = LocalDateTime.of(attendance.date, LocalTime.of(0, 0)),
                timeId = attendance.timeId
            )
        }, reason)
    }
}
