package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.utils.waitForResult
import javax.inject.Inject

class GradeStatisticsWork @Inject constructor(
    private val gradeStatisticsRepository: GradeStatisticsRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        with(gradeStatisticsRepository) {
            getGradesStatistics(student, semester, "Wszystkie", isSemester = true, forceRefresh = true).waitForResult()
            getGradesStatistics(student, semester, "Wszystkie", isSemester = false, forceRefresh = true).waitForResult()
            getGradesPointsStatistics(student, semester, "Wszystkie", forceRefresh = true).waitForResult()
        }
    }
}
