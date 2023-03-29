package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.modules.grade.GradeAverageMode.*
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GradeAverageProvider @Inject constructor(
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository
) {

    private data class AverageCalcParams(
        val gradeAverageMode: GradeAverageMode,
        val forceAverageCalc: Boolean,
        val isOptionalArithmeticAverage: Boolean,
        val plusModifier: Double,
        val minusModifier: Double,
    )

    fun getGradesDetailsWithAverage(
        student: Student,
        semesterId: Int,
        forceRefresh: Boolean
    ): Flow<Resource<List<GradeSubject>>> = combine(
        flow = preferencesRepository.gradeAverageModeFlow,
        flow2 = preferencesRepository.gradeAverageForceCalcFlow,
        flow3 = preferencesRepository.isOptionalArithmeticAverageFlow,
        flow4 = preferencesRepository.gradePlusModifierFlow,
        flow5 = preferencesRepository.gradeMinusModifierFlow,
    ) { gradeAverageMode, forceAverageCalc, isOptionalArithmeticAverage, plusModifier, minusModifier ->
        AverageCalcParams(
            gradeAverageMode = gradeAverageMode,
            forceAverageCalc = forceAverageCalc,
            isOptionalArithmeticAverage = isOptionalArithmeticAverage,
            plusModifier = plusModifier,
            minusModifier = minusModifier,
        )
    }.flatMapLatest { params ->
        flatResourceFlow {
            val semesters = semesterRepository.getSemesters(student)
            when (params.gradeAverageMode) {
                ONE_SEMESTER -> getGradeSubjects(
                    student = student,
                    semester = semesters.single { it.semesterId == semesterId },
                    forceRefresh = forceRefresh,
                    params = params,
                )
                BOTH_SEMESTERS -> calculateCombinedAverage(
                    student = student,
                    semesters = semesters,
                    semesterId = semesterId,
                    forceRefresh = forceRefresh,
                    config = params,
                )
                ALL_YEAR -> calculateCombinedAverage(
                    student = student,
                    semesters = semesters,
                    semesterId = semesterId,
                    forceRefresh = forceRefresh,
                    config = params,
                )
            }
        }
    }.distinctUntilChanged()

    private fun calculateCombinedAverage(
        student: Student,
        semesters: List<Semester>,
        semesterId: Int,
        forceRefresh: Boolean,
        config: AverageCalcParams,
    ): Flow<Resource<List<GradeSubject>>> {
        val selectedSemester = semesters.single { it.semesterId == semesterId }
        val firstSemester =
            semesters.single { it.diaryId == selectedSemester.diaryId && it.semesterName == 1 }

        val selectedSemesterGradeSubjects =
            getGradeSubjects(student, selectedSemester, forceRefresh, config)

        if (selectedSemester == firstSemester) return selectedSemesterGradeSubjects

        val firstSemesterGradeSubjects =
            getGradeSubjects(student, firstSemester, forceRefresh, config)

        return selectedSemesterGradeSubjects.combine(firstSemesterGradeSubjects) { secondSemesterGradeSubject, firstSemesterGradeSubject ->
            if (firstSemesterGradeSubject.errorOrNull != null) {
                return@combine firstSemesterGradeSubject
            }

            val isAnyVulcanAverageInFirstSemester =
                firstSemesterGradeSubject.dataOrNull.orEmpty().any { it.isVulcanAverage }
            val isAnyVulcanAverageInSecondSemester =
                secondSemesterGradeSubject.dataOrNull.orEmpty().any { it.isVulcanAverage }

            val updatedData = secondSemesterGradeSubject.dataOrNull?.map { secondSemesterSubject ->
                val firstSemesterSubject = firstSemesterGradeSubject.dataOrNull.orEmpty()
                    .singleOrNull { it.subject == secondSemesterSubject.subject }

                val updatedAverage = if (config.gradeAverageMode == ALL_YEAR) {
                    calculateAllYearAverage(
                        student = student,
                        isAnyVulcanAverage = isAnyVulcanAverageInFirstSemester || isAnyVulcanAverageInSecondSemester,
                        secondSemesterSubject = secondSemesterSubject,
                        firstSemesterSubject = firstSemesterSubject,
                        config = config,
                    )
                } else {
                    calculateBothSemestersAverage(
                        student = student,
                        isAnyVulcanAverage = isAnyVulcanAverageInFirstSemester || isAnyVulcanAverageInSecondSemester,
                        secondSemesterSubject = secondSemesterSubject,
                        firstSemesterSubject = firstSemesterSubject,
                        config = config
                    )
                }
                secondSemesterSubject.copy(average = updatedAverage)
            }
            secondSemesterGradeSubject.mapData { updatedData!! }
        }
    }

    private fun calculateAllYearAverage(
        student: Student,
        isAnyVulcanAverage: Boolean,
        secondSemesterSubject: GradeSubject,
        firstSemesterSubject: GradeSubject?,
        config: AverageCalcParams,
    ) = if (!isAnyVulcanAverage || config.forceAverageCalc) {
        val updatedSecondSemesterGrades = secondSemesterSubject.grades
            .updateModifiers(student, config)
        val updatedFirstSemesterGrades = firstSemesterSubject?.grades
            ?.updateModifiers(student, config).orEmpty()

        (updatedSecondSemesterGrades + updatedFirstSemesterGrades).calcAverage(
            config.isOptionalArithmeticAverage
        )
    } else {
        secondSemesterSubject.average
    }

    private fun calculateBothSemestersAverage(
        student: Student,
        isAnyVulcanAverage: Boolean,
        secondSemesterSubject: GradeSubject,
        firstSemesterSubject: GradeSubject?,
        config: AverageCalcParams,
    ): Double {
        return if (!isAnyVulcanAverage || config.forceAverageCalc) {
            val divider = if (secondSemesterSubject.grades.any { it.weightValue > .0 }) 2 else 1
            val secondSemesterAverage = secondSemesterSubject.grades
                .updateModifiers(student, config)
                .calcAverage(config.isOptionalArithmeticAverage)
            val firstSemesterAverage = firstSemesterSubject?.grades
                ?.updateModifiers(student, config)
                ?.calcAverage(config.isOptionalArithmeticAverage) ?: secondSemesterAverage

            (secondSemesterAverage + firstSemesterAverage) / divider
        } else {
            val divider = if (secondSemesterSubject.average > 0) 2 else 1

            secondSemesterSubject.average.plus(
                (firstSemesterSubject?.average ?: secondSemesterSubject.average)
            ) / divider
        }
    }

    private fun getGradeSubjects(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        params: AverageCalcParams,
    ): Flow<Resource<List<GradeSubject>>> {
        return gradeRepository.getGrades(student, semester, forceRefresh = forceRefresh)
            .mapResourceData { res ->
                val (details, summaries) = res
                val isAnyAverage = summaries.any { it.average != .0 }
                val allGrades = details.groupBy { it.subject }

                val items = summaries.emulateEmptySummaries(
                    student = student,
                    semester = semester,
                    grades = allGrades.toList(),
                    calcAverage = isAnyAverage,
                    params = params,
                ).map { summary ->
                    val grades = allGrades[summary.subject].orEmpty()
                    GradeSubject(
                        subject = summary.subject,
                        average = if (!isAnyAverage || params.forceAverageCalc) {
                            grades.updateModifiers(student, params)
                                .calcAverage(params.isOptionalArithmeticAverage)
                        } else summary.average,
                        points = summary.pointsSum,
                        summary = summary,
                        grades = grades,
                        isVulcanAverage = isAnyAverage
                    )
                }

                items
            }
    }

    private fun List<GradeSummary>.emulateEmptySummaries(
        student: Student,
        semester: Semester,
        grades: List<Pair<String, List<Grade>>>,
        calcAverage: Boolean,
        params: AverageCalcParams,
    ): List<GradeSummary> {
        if (isNotEmpty() && size > grades.size) return this

        return grades.mapIndexed { i, (subject, details) ->
            singleOrNull { it.subject == subject }?.let { return@mapIndexed it }
            GradeSummary(
                studentId = student.studentId,
                semesterId = semester.semesterId,
                position = i,
                subject = subject,
                predictedGrade = "",
                finalGrade = "",
                proposedPoints = "",
                finalPoints = "",
                pointsSum = "",
                average = if (calcAverage) details.updateModifiers(student, params)
                    .calcAverage(params.isOptionalArithmeticAverage) else .0
            )
        }
    }

    private fun List<Grade>.updateModifiers(
        student: Student,
        params: AverageCalcParams,
    ): List<Grade> = if (student.loginMode == Sdk.Mode.SCRAPPER.name) {
        map { it.changeModifier(params.plusModifier, params.minusModifier) }
    } else this
}
