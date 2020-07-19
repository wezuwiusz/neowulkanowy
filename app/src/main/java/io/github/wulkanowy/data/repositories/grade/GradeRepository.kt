package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(
    private val local: GradeLocal,
    private val remote: GradeRemote
) {

    fun getGrades(student: Student, semester: Semester, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        shouldFetch = { (details, summaries) -> details.isEmpty() || summaries.isEmpty() || forceRefresh },
        query = { local.getGradesDetails(semester).combine(local.getGradesSummary(semester)) { details, summaries -> details to summaries } },
        fetch = { remote.getGrades(student, semester) },
        saveFetchResult = { old, new ->
            refreshGradeDetails(student, old.first, new.first, notify)
            refreshGradeSummaries(old.second, new.second, notify)
        }
    )

    private suspend fun refreshGradeDetails(student: Student, oldGrades: List<Grade>, newDetails: List<Grade>, notify: Boolean) {
        val notifyBreakDate = oldGrades.maxBy { it.date }?.date ?: student.registrationDate.toLocalDate()
        local.deleteGrades(oldGrades uniqueSubtract newDetails)
        local.saveGrades((newDetails uniqueSubtract oldGrades).onEach {
            if (it.date >= notifyBreakDate) it.apply {
                isRead = false
                if (notify) isNotified = false
            }
        })
    }

    private suspend fun refreshGradeSummaries(oldSummaries: List<GradeSummary>, newSummary: List<GradeSummary>, notify: Boolean) {
        local.deleteGradesSummary(oldSummaries uniqueSubtract newSummary)
        local.saveGradesSummary((newSummary uniqueSubtract oldSummaries).onEach { summary ->
            val oldSummary = oldSummaries.find { oldSummary -> oldSummary.subject == summary.subject }
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
                oldSummary == null -> LocalDateTime.now()
                summary.predictedGrade != oldSummary.predictedGrade -> LocalDateTime.now()
                else -> oldSummary.predictedGradeLastChange
            }
            summary.finalGradeLastChange = when {
                oldSummary == null -> LocalDateTime.now()
                summary.finalGrade != oldSummary.finalGrade -> LocalDateTime.now()
                else -> oldSummary.finalGradeLastChange
            }
        })
    }

    fun getUnreadGrades(semester: Semester): Flow<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isRead } }
    }

    fun getNotNotifiedGrades(semester: Semester): Flow<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isNotified } }
    }

    fun getNotNotifiedPredictedGrades(semester: Semester): Flow<List<GradeSummary>> {
        return local.getGradesSummary(semester).map { it.filter { gradeSummary -> !gradeSummary.isPredictedGradeNotified } }
    }

    fun getNotNotifiedFinalGrades(semester: Semester): Flow<List<GradeSummary>> {
        return local.getGradesSummary(semester).map { it.filter { gradeSummary -> !gradeSummary.isFinalGradeNotified } }
    }

    suspend fun updateGrade(grade: Grade) {
        return local.updateGrades(listOf(grade))
    }

    suspend fun updateGrades(grades: List<Grade>) {
        return local.updateGrades(grades)
    }

    suspend fun updateGradesSummary(gradesSummary: List<GradeSummary>) {
        return local.updateGradesSummary(gradesSummary)
    }
}
