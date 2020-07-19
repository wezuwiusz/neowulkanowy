package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
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

    fun getAttendance(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getAttendance(semester, start.monday, end.sunday) },
        fetch = { remote.getAttendance(student, semester, start.monday, end.sunday) },
        saveFetchResult = { old, new ->
            local.deleteAttendance(old uniqueSubtract new)
            local.saveAttendance(new uniqueSubtract old)
        },
        filterResult = { it.filter { item -> item.date in start..end } }
    )

    suspend fun excuseForAbsence(student: Student, semester: Semester, attendanceList: List<Attendance>, reason: String? = null) {
        remote.excuseAbsence(student, semester, attendanceList, reason)
    }
}
