package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.reactivex.Completable
import javax.inject.Inject

class GradeSummaryWork @Inject constructor(private val gradeSummaryRepository: GradeSummaryRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return gradeSummaryRepository.getGradesSummary(student, semester, true).ignoreElement()
    }
}
