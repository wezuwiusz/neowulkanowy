package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import io.reactivex.Single
import javax.inject.Inject

class GradeAverageProvider @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val gradeRepository: GradeRepository
) {
    fun getGradeAverage(student: Student, semesters: List<Semester>, selectedSemesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        return when (preferencesRepository.gradeAverageMode) {
            "all_year" -> getAllYearAverage(student, semesters, selectedSemesterId, forceRefresh)
            "only_one_semester" -> getOnlyOneSemesterAverage(student, semesters, selectedSemesterId, forceRefresh)
            else -> throw IllegalArgumentException("Incorrect grade average mode: ${preferencesRepository.gradeAverageMode} ")
        }
    }

    private fun getAllYearAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val firstSemester = semesters.single { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }
        val plusModifier = preferencesRepository.gradePlusModifier
        val minusModifier = preferencesRepository.gradeMinusModifier

        return gradeRepository.getGrades(student, selectedSemester, forceRefresh)
            .flatMap { firstGrades ->
                if (selectedSemester == firstSemester) Single.just(firstGrades)
                else gradeRepository.getGrades(student, firstSemester)
                    .map { secondGrades -> secondGrades + firstGrades }
            }.map { grades ->
                grades.map { it.changeModifier(plusModifier, minusModifier) }
                    .groupBy { it.subject }
                    .mapValues { it.value.calcAverage() }
            }
    }

    private fun getOnlyOneSemesterAverage(student: Student, semesters: List<Semester>, semesterId: Int, forceRefresh: Boolean): Single<Map<String, Double>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val plusModifier = preferencesRepository.gradePlusModifier
        val minusModifier = preferencesRepository.gradeMinusModifier

        return gradeRepository.getGrades(student, selectedSemester, forceRefresh)
            .map { grades ->
                grades.map { it.changeModifier(plusModifier, minusModifier) }
                    .groupBy { it.subject }
                    .mapValues { it.value.calcAverage() }
            }
    }
}
