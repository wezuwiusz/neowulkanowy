package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val local: GradeStatisticsLocal,
    private val remote: GradeStatisticsRemote
) {

    fun getGradesPartialStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getGradePartialStatistics(semester) },
        fetch = { remote.getGradePartialStatistics(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteGradePartialStatistics(old uniqueSubtract new)
            local.saveGradePartialStatistics(new uniqueSubtract old)
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> {
                    val numerator = items.map {
                        it.classAverage.replace(",", ".").toDoubleOrNull() ?: .0
                    }.filterNot { it == .0 }
                    (items.reversed() + GradePartialStatistics(
                        studentId = semester.studentId,
                        semesterId = semester.semesterId,
                        subject = subjectName,
                        classAverage = if (numerator.isEmpty()) "" else numerator.average().let {
                            "%.2f".format(Locale.FRANCE, it)
                        },
                        studentAverage = "",
                        classAmounts = items.map { it.classAmounts }.sumGradeAmounts(),
                        studentAmounts = items.map { it.studentAmounts }.sumGradeAmounts()
                    )).reversed()
                }
                else -> items.filter { it.subject == subjectName }
            }.mapPartialToStatisticItems()
        }
    )

    fun getGradesSemesterStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getGradeSemesterStatistics(semester) },
        fetch = { remote.getGradeSemesterStatistics(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteGradeSemesterStatistics(old uniqueSubtract new)
            local.saveGradeSemesterStatistics(new uniqueSubtract old)
        },
        mapResult = { items ->
            val itemsWithAverage = items.map { item ->
                item.copy().apply {
                    val denominator = item.amounts.sum()
                    average = if (denominator == 0) "" else (item.amounts.mapIndexed { gradeValue, amount ->
                        (gradeValue + 1) * amount
                    }.sum().toDouble() / denominator).let {
                        "%.2f".format(Locale.FRANCE, it)
                    }
                }
            }
            when (subjectName) {
                "Wszystkie" -> (itemsWithAverage.reversed() + GradeSemesterStatistics(
                    studentId = semester.studentId,
                    semesterId = semester.semesterId,
                    subject = subjectName,
                    amounts = itemsWithAverage.map { it.amounts }.sumGradeAmounts(),
                    studentGrade = 0
                ).apply {
                    average = itemsWithAverage.mapNotNull { it.average.replace(",", ".").toDoubleOrNull() }.average().let {
                        "%.2f".format(Locale.FRANCE, it)
                    }
                }).reversed()
                else -> itemsWithAverage.filter { it.subject == subjectName }
            }.mapSemesterToStatisticItems()
        }
    )

    fun getGradesPointsStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getGradePointsStatistics(semester) },
        fetch = { remote.getGradePointsStatistics(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteGradePointsStatistics(old uniqueSubtract new)
            local.saveGradePointsStatistics(new uniqueSubtract old)
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> items
                else -> items.filter { it.subject == subjectName }
            }.mapPointsToStatisticsItems()
        }
    )

    private fun List<List<Int>>.sumGradeAmounts(): List<Int> {
        val result = mutableListOf(0, 0, 0, 0, 0, 0)
        forEach {
            it.forEachIndexed { grade, amount ->
                result[grade] += amount
            }
        }
        return result
    }

    private fun List<GradePartialStatistics>.mapPartialToStatisticItems() = filterNot { it.classAmounts.isEmpty() }.map {
        GradeStatisticsItem(
            type = ViewType.PARTIAL,
            average = it.classAverage,
            partial = it,
            points = null,
            semester = null
        )
    }

    private fun List<GradeSemesterStatistics>.mapSemesterToStatisticItems() = filterNot { it.amounts.isEmpty() }.map {
        GradeStatisticsItem(
            type = ViewType.SEMESTER,
            partial = null,
            points = null,
            average = "",
            semester = it
        )
    }

    private fun List<GradePointsStatistics>.mapPointsToStatisticsItems() = map {
        GradeStatisticsItem(
            type = ViewType.POINTS,
            partial = null,
            semester = null,
            average = "",
            points = it
        )
    }
}
