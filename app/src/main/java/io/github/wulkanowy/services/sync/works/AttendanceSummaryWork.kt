package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.attendancesummary.AttendanceSummaryRepository
import io.reactivex.Completable
import javax.inject.Inject

class AttendanceSummaryWork @Inject constructor(
    private val attendanceSummaryRepository: AttendanceSummaryRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return attendanceSummaryRepository.getAttendanceSummary(semester, -1, true).ignoreElement()
    }
}

