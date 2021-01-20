package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.utils.waitForResult
import javax.inject.Inject

class AttendanceSummaryWork @Inject constructor(
    private val attendanceSummaryRepository: AttendanceSummaryRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        attendanceSummaryRepository.getAttendanceSummary(student, semester, -1, true).waitForResult()
    }
}
