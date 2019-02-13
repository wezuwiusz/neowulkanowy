package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceLocal @Inject constructor(private val attendanceDb: AttendanceDao) {

    fun getAttendance(semester: Semester, startDate: LocalDate, endDate: LocalDate): Maybe<List<Attendance>> {
        return attendanceDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
                .filter { !it.isEmpty() }
    }

    fun saveAttendance(attendance: List<Attendance>) {
        attendanceDb.insertAll(attendance)
    }

    fun deleteAttendance(attendance: List<Attendance>) {
        attendanceDb.deleteAll(attendance)
    }
}
