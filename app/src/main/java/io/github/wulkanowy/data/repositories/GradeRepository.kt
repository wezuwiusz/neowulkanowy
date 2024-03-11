package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeDescriptiveDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeDescriptive
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.toLocalDate
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(
    private val gradeDb: GradeDao,
    private val gradeSummaryDb: GradeSummaryDao,
    private val gradeDescriptiveDb: GradeDescriptiveDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    fun getGrades(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        notify: Boolean = false,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = {
            //When details is empty and summary is not, app will not use summary cache - edge case
            it.first.isEmpty()
        },
        shouldFetch = { (details, summaries, descriptive) ->
            val isExpired =
                refreshHelper.shouldBeRefreshed(getRefreshKey(GRADE_CACHE_KEY, semester))
            details.isEmpty() || (summaries.isEmpty() && descriptive.isEmpty()) || forceRefresh || isExpired
        },
        query = {
            val detailsFlow = gradeDb.loadAll(semester.semesterId, semester.studentId)
            val summaryFlow = gradeSummaryDb.loadAll(semester.semesterId, semester.studentId)
            val descriptiveFlow =
                gradeDescriptiveDb.loadAll(semester.semesterId, semester.studentId)

            combine(detailsFlow, summaryFlow, descriptiveFlow) { details, summaries, descriptive ->
                Triple(details, summaries, descriptive)
            }
        },
        fetch = {
            val (details, summary, descriptive) = wulkanowySdkFactory.create(student, semester)
                .getGrades(semester.semesterId)

            Triple(
                details.mapToEntities(semester),
                summary.mapToEntities(semester),
                descriptive.mapToEntities(semester)
            )
        },
        saveFetchResult = { (oldDetails, oldSummary, oldDescriptive), (newDetails, newSummary, newDescriptive) ->
            refreshGradeDetails(student, oldDetails, newDetails, notify)
            refreshGradeSummaries(oldSummary, newSummary, notify)
            refreshGradeDescriptions(oldDescriptive, newDescriptive, notify)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(GRADE_CACHE_KEY, semester))
        }
    )

    private suspend fun refreshGradeDescriptions(
        old: List<GradeDescriptive>,
        new: List<GradeDescriptive>,
        notify: Boolean
    ) {
        gradeDescriptiveDb.removeOldAndSaveNew(
            oldItems = old uniqueSubtract new,
            newItems = (new uniqueSubtract old).onEach {
                if (notify) it.isNotified = false
            },
        )
    }

    private suspend fun refreshGradeDetails(
        student: Student,
        oldGrades: List<Grade>,
        newDetails: List<Grade>,
        notify: Boolean
    ) {
        val notifyBreakDate = oldGrades.maxByOrNull { it.date }?.date
            ?: student.registrationDate.toLocalDate()

        gradeDb.removeOldAndSaveNew(
            oldItems = oldGrades uniqueSubtract newDetails,
            newItems = (newDetails uniqueSubtract oldGrades).onEach {
                if (it.date >= notifyBreakDate) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            },
        )
    }

    private suspend fun refreshGradeSummaries(
        oldSummaries: List<GradeSummary>,
        newSummary: List<GradeSummary>,
        notify: Boolean
    ) {
        gradeSummaryDb.removeOldAndSaveNew(
            oldItems = oldSummaries uniqueSubtract newSummary,
            newItems = (newSummary uniqueSubtract oldSummaries).onEach { summary ->
                getGradeSummaryWithUpdatedNotificationState(
                    summary = summary,
                    oldSummary = oldSummaries.find { it.subject == summary.subject },
                    notify = notify,
                )
            },
        )
    }

    private fun getGradeSummaryWithUpdatedNotificationState(
        summary: GradeSummary,
        oldSummary: GradeSummary?,
        notify: Boolean,
    ) {
        summary.isPredictedGradeNotified = when {
            summary.predictedGrade.isEmpty() -> true
            notify && oldSummary?.predictedGrade != summary.predictedGrade -> false
            else -> true
        }
        summary.isFinalGradeNotified = when {
            summary.finalGrade.isEmpty() -> true
            notify && oldSummary?.finalGrade != summary.finalGrade -> false
            else -> true
        }
        summary.predictedGradeLastChange = when {
            oldSummary == null -> Instant.now()
            summary.predictedGrade != oldSummary.predictedGrade -> Instant.now()
            else -> oldSummary.predictedGradeLastChange
        }
        summary.finalGradeLastChange = when {
            oldSummary == null -> Instant.now()
            summary.finalGrade != oldSummary.finalGrade -> Instant.now()
            else -> oldSummary.finalGradeLastChange
        }
    }

    fun getUnreadGrades(semester: Semester): Flow<List<Grade>> {
        return gradeDb.loadAll(semester.semesterId, semester.studentId).map {
            it.filter { grade -> !grade.isRead }
        }
    }

    fun getGradesFromDatabase(semester: Semester): Flow<List<Grade>> {
        return gradeDb.loadAll(semester.semesterId, semester.studentId)
    }

    fun getGradesPredictedFromDatabase(semester: Semester): Flow<List<GradeSummary>> {
        return gradeSummaryDb.loadAll(semester.semesterId, semester.studentId)
    }

    fun getGradesFinalFromDatabase(semester: Semester): Flow<List<GradeSummary>> {
        return gradeSummaryDb.loadAll(semester.semesterId, semester.studentId)
    }

    fun getGradesDescriptiveFromDatabase(semester: Semester): Flow<List<GradeDescriptive>> {
        return gradeDescriptiveDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun updateGrade(grade: Grade) {
        return gradeDb.updateAll(listOf(grade))
    }

    suspend fun updateGrades(grades: List<Grade>) {
        return gradeDb.updateAll(grades)
    }

    suspend fun updateGradesSummary(gradesSummary: List<GradeSummary>) {
        return gradeSummaryDb.updateAll(gradesSummary)
    }

    suspend fun updateGradesDescriptive(gradesDescriptive: List<GradeDescriptive>) {
        return gradeDescriptiveDb.updateAll(gradesDescriptive)
    }

    private companion object {

        private const val GRADE_CACHE_KEY = "grade"
    }
}
