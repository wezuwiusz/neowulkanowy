package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class GradeAverageProvider @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val gradeRepository: GradeRepository,
    private val gradeSummaryRepository: GradeSummaryRepository
) {

    private val plusModifier = preferencesRepository.gradePlusModifier

    private val minusModifier = preferencesRepository.gradeMinusModifier

    fun getGradeAverage(student: Student, semesters: List<Semester>, selectedSemesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        return when (preferencesRepository.gradeAverageMode) {
            "all_year" -> getAllYearAverage(student, semesters, selectedSemesterId, forceRefresh)
            "only_one_semester" -> getOnlyOneSemesterAverage(student, semesters, selectedSemesterId, forceRefresh)
            else -> throw IllegalArgumentException("Incorrect grade average mode: ${preferencesRepository.gradeAverageMode} ")
        }
    }

    private fun getAllYearAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val firstSemester = semesters.single { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }

        return getAverageFromGradeSummary(student, selectedSemester, forceRefresh)
            .switchIfEmpty(gradeRepository.getGrades(student, selectedSemester, forceRefresh)
                .flatMap { firstGrades ->
                    if (selectedSemester == firstSemester) Single.just(firstGrades)
                    else {
                        gradeRepository.getGrades(student, firstSemester)
                            .map { secondGrades -> secondGrades + firstGrades }
                    }
                }.map { grades ->
                    grades.map { if (student.loginMode == Sdk.Mode.SCRAPPER.name) it.changeModifier(plusModifier, minusModifier) else it }
                        .groupBy { it.subject }
                        .map { Triple(it.key, it.value.calcAverage(), "") }
                })
    }

    private fun getOnlyOneSemesterAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<List<Triple<String, Double, String>>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }

        return getAverageFromGradeSummary(student, selectedSemester, forceRefresh)
            .switchIfEmpty(gradeRepository.getGrades(student, selectedSemester, forceRefresh)
                .map { grades ->
                    grades.map { if (student.loginMode == Sdk.Mode.SCRAPPER.name) it.changeModifier(plusModifier, minusModifier) else it }
                        .groupBy { it.subject }
                        .map { Triple(it.key, it.value.calcAverage(), "") }
                })
    }

    private fun getAverageFromGradeSummary(student: Student, selectedSemester: Semester, forceRefresh: Boolean): Maybe<List<Triple<String, Double, String>>> {
        return gradeSummaryRepository.getGradesSummary(student, selectedSemester, forceRefresh)
            .toMaybe()
            .flatMap {
                if (it.any { summary -> summary.average != .0 }) {
                    Maybe.just(it.map { summary -> Triple(summary.subject, summary.average, summary.pointsSum) })
                } else Maybe.empty()
            }.filter { !preferencesRepository.gradeAverageForceCalc }
    }
}
