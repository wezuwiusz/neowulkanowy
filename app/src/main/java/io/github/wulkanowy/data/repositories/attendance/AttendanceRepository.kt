package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val local: AttendanceLocal,
    private val remote: AttendanceRemote
) {

    suspend fun getAttendance(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean): List<Attendance> {
        return local.getAttendance(semester, start.monday, end.sunday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getAttendance(student, semester, start.monday, end.sunday)
            val old = local.getAttendance(semester, start.monday, end.sunday)

            local.deleteAttendance(old.uniqueSubtract(new))
            local.saveAttendance(new.uniqueSubtract(old))

            local.getAttendance(semester, start.monday, end.sunday)
        }.filter { it.date in start..end }
    }

    suspend fun excuseForAbsence(student: Student, semester: Semester, attendanceList: List<Attendance>, reason: String? = null): Boolean {
        return remote.excuseAbsence(student, semester, attendanceList, reason)
    }
}
