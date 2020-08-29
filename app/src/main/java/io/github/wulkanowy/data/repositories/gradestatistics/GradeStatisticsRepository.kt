package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val local: GradeStatisticsLocal,
    private val remote: GradeStatisticsRemote
) {

    fun getGradesStatistics(student: Student, semester: Semester, subjectName: String, isSemester: Boolean, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getGradesStatistics(semester, isSemester) },
        fetch = { remote.getGradeStatistics(student, semester, isSemester) },
        saveFetchResult = { old, new ->
            local.deleteGradesStatistics(old uniqueSubtract new)
            local.saveGradesStatistics(new uniqueSubtract old)
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> items.groupBy { it.grade }.map {
                    GradeStatistics(semester.studentId, semester.semesterId, subjectName, it.key,
                        it.value.fold(0) { acc, e -> acc + e.amount }, false)
                } + items
                else -> items.filter { it.subject == subjectName }
            }.mapToStatisticItems()
        }
    )

    fun getGradesPointsStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getGradesPointsStatistics(semester) },
        fetch = { remote.getGradePointsStatistics(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteGradesPointsStatistics(old uniqueSubtract new)
            local.saveGradesPointsStatistics(new uniqueSubtract old)
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> items
                else -> items.filter { it.subject == subjectName }
            }.mapToStatisticsItem()
        }
    )

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
