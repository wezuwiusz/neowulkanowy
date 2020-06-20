package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(
    private val local: GradeLocal,
    private val remote: GradeRemote
) {

    suspend fun getGrades(student: Student, semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): Pair<List<Grade>, List<GradeSummary>> {
        val details = local.getGradesDetails(semester)
        val summaries = local.getGradesSummary(semester)

        if ((details.isNotEmpty() || summaries.isNotEmpty()) && !forceRefresh) {
            return details to summaries
        }

        val (newDetails, newSummary) = remote.getGrades(student, semester)
        val oldGrades = local.getGradesDetails(semester)

        val notifyBreakDate = oldGrades.maxBy { it.date }?.date ?: student.registrationDate.toLocalDate()
        local.deleteGrades(oldGrades.uniqueSubtract(newDetails))
        local.saveGrades(newDetails.uniqueSubtract(oldGrades).onEach {
                if (it.date >= notifyBreakDate) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            })

        val oldSummaries = local.getGradesSummary(semester)

        local.deleteGradesSummary(oldSummaries.uniqueSubtract(newSummary))
        local.saveGradesSummary(newSummary.uniqueSubtract(oldSummaries).onEach { summary ->
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

        return local.getGradesDetails(semester) to local.getGradesSummary(semester)
    }

    suspend fun getUnreadGrades(semester: Semester): List<Grade> {
        return local.getGradesDetails(semester).filter { grade -> !grade.isRead }
    }

    suspend fun getNotNotifiedGrades(semester: Semester): List<Grade> {
        return local.getGradesDetails(semester).filter { grade -> !grade.isNotified }
    }

    suspend fun getNotNotifiedPredictedGrades(semester: Semester): List<GradeSummary> {
        return local.getGradesSummary(semester).filter { gradeSummary -> !gradeSummary.isPredictedGradeNotified }
    }

    suspend fun getNotNotifiedFinalGrades(semester: Semester): List<GradeSummary> {
        return local.getGradesSummary(semester).filter { gradeSummary -> !gradeSummary.isFinalGradeNotified }
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
