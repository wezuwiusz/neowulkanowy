package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.GradePartialStatisticsDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSemesterStatisticsDao
import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapPartialToStatisticItems
import io.github.wulkanowy.data.mappers.mapPointsToStatisticsItems
import io.github.wulkanowy.data.mappers.mapSemesterToStatisticItems
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.sync.Mutex
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val gradePartialStatisticsDb: GradePartialStatisticsDao,
    private val gradePointsStatisticsDb: GradePointsStatisticsDao,
    private val gradeSemesterStatisticsDb: GradeSemesterStatisticsDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val partialMutex = Mutex()
    private val semesterMutex = Mutex()
    private val pointsMutex = Mutex()

    private val partialCacheKey = "grade_stats_partial"
    private val semesterCacheKey = "grade_stats_semester"
    private val pointsCacheKey = "grade_stats_points"

    fun getGradesPartialStatistics(
        student: Student,
        semester: Semester,
        subjectName: String,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = partialMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(partialCacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradePartialStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getGradesPartialStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradePartialStatisticsDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = new uniqueSubtract old,
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(partialCacheKey, semester))
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> {
                    val summaryItem = GradePartialStatistics(
                        studentId = semester.studentId,
                        semesterId = semester.semesterId,
                        subject = subjectName,
                        classAverage = items.map { it.classAverage }.getSummaryAverage(),
                        studentAverage = items.map { it.studentAverage }.getSummaryAverage(),
                        classAmounts = items.map { it.classAmounts }.sumGradeAmounts(),
                        studentAmounts = items.map { it.studentAmounts }.sumGradeAmounts()
                    )
                    listOf(summaryItem) + items
                }

                else -> items.filter { it.subject == subjectName }
            }.mapPartialToStatisticItems()
        }
    )

    fun getGradesSemesterStatistics(
        student: Student,
        semester: Semester,
        subjectName: String,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = semesterMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(semesterCacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradeSemesterStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getGradesSemesterStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradeSemesterStatisticsDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = new uniqueSubtract old,
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(semesterCacheKey, semester))
        },
        mapResult = { items ->
            val itemsWithAverage = items.map { item ->
                item.copy().apply {
                    val denominator = item.amounts.sum()
                    classAverage = if (denominator == 0) "" else {
                        (item.amounts.mapIndexed { gradeValue, amount ->
                            (gradeValue + 1) * amount
                        }.sum().toDouble() / denominator).asAverageString()
                    }
                }
            }
            when (subjectName) {
                "Wszystkie" -> {
                    val summaryItem = GradeSemesterStatistics(
                        studentId = semester.studentId,
                        semesterId = semester.semesterId,
                        subject = subjectName,
                        amounts = itemsWithAverage.map { it.amounts }.sumGradeAmounts(),
                        studentGrade = 0,
                    ).apply {
                        classAverage = itemsWithAverage.map { it.classAverage }.getSummaryAverage()
                        studentAverage = items
                            .mapNotNull { summary -> summary.studentGrade.takeIf { it != 0 } }
                            .average().asAverageString()
                    }
                    listOf(summaryItem) + itemsWithAverage
                }

                else -> itemsWithAverage.filter { it.subject == subjectName }
            }.mapSemesterToStatisticItems()
        }
    )

    fun getGradesPointsStatistics(
        student: Student,
        semester: Semester,
        subjectName: String,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = pointsMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(pointsCacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getGradesPointsStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradePointsStatisticsDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = new uniqueSubtract old,
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(pointsCacheKey, semester))
        },
        mapResult = { items ->
            when (subjectName) {
                "Wszystkie" -> items
                else -> items.filter { it.subject == subjectName }
            }.mapPointsToStatisticsItems()
        }
    )

    private fun List<String>.getSummaryAverage(): String {
        val averages = mapNotNull {
            it.replace(",", ".").toDoubleOrNull()
        }

        return averages.average()
            .asAverageString()
            .takeIf { averages.isNotEmpty() }
            .orEmpty()
    }

    private fun Double.asAverageString(): String = "%.2f".format(Locale.FRANCE, this)

    private fun List<List<Int>>.sumGradeAmounts(): List<Int> {
        val result = mutableListOf(0, 0, 0, 0, 0, 0)
        forEach {
            it.forEachIndexed { grade, amount ->
                result[grade] += amount
            }
        }
        return result
    }
}
