package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val local: GradeStatisticsLocal,
    private val remote: GradeStatisticsRemote
) {

    suspend fun getGradesStatistics(student: Student, semester: Semester, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false): List<GradeStatisticsItem> {
        return local.getGradesStatistics(semester, isSemester, subjectName).mapToStatisticItems().filter { !forceRefresh }.ifEmpty {
            val new = remote.getGradeStatistics(student, semester, isSemester)
            val old = local.getGradesStatistics(semester, isSemester)

            local.deleteGradesStatistics(old.uniqueSubtract(new))
            local.saveGradesStatistics(new.uniqueSubtract(old))

            local.getGradesStatistics(semester, isSemester, subjectName).mapToStatisticItems()
        }
    }

    suspend fun getGradesPointsStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean): List<GradeStatisticsItem> {
        return local.getGradesPointsStatistics(semester, subjectName).mapToStatisticsItem().filter { !forceRefresh }.ifEmpty {
            val new = remote.getGradePointsStatistics(student, semester)
            val old = local.getGradesPointsStatistics(semester)

            local.deleteGradesPointsStatistics(old.uniqueSubtract(new))
            local.saveGradesPointsStatistics(new.uniqueSubtract(old))

            local.getGradesPointsStatistics(semester, subjectName).mapToStatisticsItem()
        }
    }

    private fun List<GradeStatistics>.mapToStatisticItems() = groupBy { it.subject }.map {
        GradeStatisticsItem(
            type = ViewType.PARTIAL,
            partial = it.value
                .sortedByDescending { item -> item.grade }
                .filter { item -> item.amount != 0 },
            points = null
        )
    }

    private fun List<GradePointsStatistics>.mapToStatisticsItem() = map {
        GradeStatisticsItem(
            type = ViewType.POINTS,
            partial = emptyList(),
            points = it
        )
    }
}
