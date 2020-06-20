package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val local: AttendanceSummaryLocal,
    private val remote: AttendanceSummaryRemote
) {

    suspend fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int, forceRefresh: Boolean = false): List<AttendanceSummary> {
        return local.getAttendanceSummary(semester, subjectId).filter { !forceRefresh }.ifEmpty {
            val new = remote.getAttendanceSummary(student, semester, subjectId)

            val old = local.getAttendanceSummary(semester, subjectId)
            local.deleteAttendanceSummary(old.uniqueSubtract(new))
            local.saveAttendanceSummary(new.uniqueSubtract(old))

            return local.getAttendanceSummary(semester, subjectId)
        }
    }
}
