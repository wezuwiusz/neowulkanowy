package io.github.wulkanowy.data.repositories.attendancesummary

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val local: AttendanceSummaryLocal,
    private val remote: AttendanceSummaryRemote
) {

    fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getAttendanceSummary(semester, subjectId) },
        fetch = { remote.getAttendanceSummary(student, semester, subjectId) },
        saveFetchResult = { old, new ->
            local.deleteAttendanceSummary(old uniqueSubtract new)
            local.saveAttendanceSummary(new uniqueSubtract old)
        }
    )
}
