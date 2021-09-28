package io.github.wulkanowy.data.repositories

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
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
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
    private val sdk: Sdk,
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
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(partialCacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradePartialStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getGradesPartialStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradePartialStatisticsDb.deleteAll(old uniqueSubtract new)
            gradePartialStatisticsDb.insertAll(new uniqueSubtract old)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(partialCacheKey, semester))
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

    fun getGradesSemesterStatistics(
        student: Student,
        semester: Semester,
        subjectName: String,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = semesterMutex,
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(semesterCacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradeSemesterStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getGradesSemesterStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradeSemesterStatisticsDb.deleteAll(old uniqueSubtract new)
            gradeSemesterStatisticsDb.insertAll(new uniqueSubtract old)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(semesterCacheKey, semester))
        },
        mapResult = { items ->
            val itemsWithAverage = items.map { item ->
                item.copy().apply {
                    val denominator = item.amounts.sum()
                    average = if (denominator == 0) "" else {
                        (item.amounts.mapIndexed { gradeValue, amount ->
                            (gradeValue + 1) * amount
                        }.sum().toDouble() / denominator).let {
                            "%.2f".format(Locale.FRANCE, it)
                        }
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
                    average = itemsWithAverage.mapNotNull {
                        it.average.replace(",", ".").toDoubleOrNull()
                    }.average().let {
                        "%.2f".format(Locale.FRANCE, it)
                    }
                }).reversed()
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
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(pointsCacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getGradesPointsStatistics(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            gradePointsStatisticsDb.deleteAll(old uniqueSubtract new)
            gradePointsStatisticsDb.insertAll(new uniqueSubtract old)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(pointsCacheKey, semester))
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
}
