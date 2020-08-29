package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryLocal @Inject constructor(private val attendanceDb: AttendanceSummaryDao) {

    suspend fun saveAttendanceSummary(attendance: List<AttendanceSummary>) {
        attendanceDb.insertAll(attendance)
    }

    suspend fun deleteAttendanceSummary(attendance: List<AttendanceSummary>) {
        attendanceDb.deleteAll(attendance)
    }

    fun getAttendanceSummary(semester: Semester, subjectId: Int): Flow<List<AttendanceSummary>> {
        return attendanceDb.loadAll(semester.diaryId, semester.studentId, subjectId)
    }
}
