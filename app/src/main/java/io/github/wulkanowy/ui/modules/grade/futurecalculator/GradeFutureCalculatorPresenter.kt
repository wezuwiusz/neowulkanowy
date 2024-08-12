package io.github.wulkanowy.ui.modules.grade.futurecalculator

import android.widget.TextView
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.flatResourceFlow
import io.github.wulkanowy.data.mapResourceData
import io.github.wulkanowy.data.onResourceData
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.SubjectRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.calcAverage
import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDate
import javax.inject.Inject

class GradeFutureCalculatorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) : BasePresenter<GradeFutureCalculatorView>(errorHandler, studentRepository) {

    private var grades: List<Grade> = emptyList()

    override fun onAttachView(view: GradeFutureCalculatorView) {
        super.onAttachView(view)
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            subjectRepository.getSubjects(student, semester)
        }
            .mapResourceData {
                it.map { subject ->
                    subject.name
                }
            }
            .onResourceData {
                view.initView(it.toTypedArray())
                Timber.i("Grade future calculator view was initialized")
            }
            .launch("subjects")

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            gradeRepository.getGrades(student, semester, false)
        }
            .onResourceData {
                grades = it.first
            }
            .launch("grades")
    }

    fun onInput(
        subject: String?,
        grade: String?,
        weight: String?,
        gradeFutureCalculatorValue: TextView
    ) {
        var isError = false

        if (subject.isNullOrBlank()) {
            view?.setErrorSubjectRequired()
            isError = true
        }

        if (grade.isNullOrBlank()) {
            view?.setErrorGradeRequired()
            isError = true
        }

        if (weight.isNullOrBlank()) {
            view?.setErrorWeightRequired()
            isError = true
        }

        if (!isError) {
            calculateWeights(subject!!, grade!!, weight!!, gradeFutureCalculatorValue)
        }
    }

    private fun calculateWeights(
        subject: String,
        grade: String,
        weight: String,
        gradeFutureCalculatorValue: TextView
    ) {
        val grades = grades.filter {
            it.subject == subject
        }

        val gradesWithNewGrade = grades.toMutableList()
        gradesWithNewGrade.add(
            Grade(
                semesterId = gradesWithNewGrade.first().semesterId,
                studentId = gradesWithNewGrade.first().studentId,
                date = LocalDate.now(),
                value = grade.toDouble(),
                weight = weight,
                weightValue = weight.toDouble(),
                color = "",
                comment = "",
                description = "",
                entry = grade,
                gradeSymbol = grade,
                teacher = "",
                modifier = 0.0,
                subject = subject
            )
        )

        val formatter = DecimalFormat("#0.00")
        val average = formatter.format(gradesWithNewGrade.toList().calcAverage(true))
        gradeFutureCalculatorValue.text = average
    }
}
