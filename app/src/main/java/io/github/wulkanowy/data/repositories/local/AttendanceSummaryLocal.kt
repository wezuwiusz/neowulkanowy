package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject

class AttendanceSummaryLocal @Inject constructor(private val attendanceDb: AttendanceSummaryDao) {

    fun getAttendanceSummary(semester: Semester, subjectId: Int): Maybe<List<AttendanceSummary>> {
        return attendanceDb.loadAll(semester.diaryId, semester.studentId, subjectId).filter { !it.isEmpty() }
    }

    fun saveAttendanceSummary(attendance: List<AttendanceSummary>) {
        attendanceDb.insertAll(attendance)
    }

    fun deleteAttendanceSummary(attendance: List<AttendanceSummary>) {
        attendanceDb.deleteAll(attendance)
    }
}
