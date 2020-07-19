package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceLocal @Inject constructor(private val attendanceDb: AttendanceDao) {

    suspend fun saveAttendance(attendance: List<Attendance>) {
        attendanceDb.insertAll(attendance)
    }

    suspend fun deleteAttendance(attendance: List<Attendance>) {
        attendanceDb.deleteAll(attendance)
    }

    fun getAttendance(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<Attendance>> {
        return attendanceDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
    }
}
