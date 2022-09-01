package io.github.wulkanowy.ui.modules.grade.details

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.enums.GradeExpandMode
import io.github.wulkanowy.data.enums.GradeSortingMode.*
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.ui.modules.grade.GradeSubject
import io.github.wulkanowy.utils.AnalyticsHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeRepository: GradeRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val averageProvider: GradeAverageProvider,
    private val analytics: AnalyticsHelper
) : BasePresenter<GradeDetailsView>(errorHandler, studentRepository) {

    private var newGradesAmount: Int = 0

    private var currentSemesterId = 0

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeDetailsView) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId

        if (!forceRefresh) view?.showErrorView(false)
        loadData(semesterId, forceRefresh)
    }

    fun onGradeItemSelected(grade: Grade, position: Int) {
        Timber.i("Select grade item ${grade.id}, position: $position")
        view?.apply {
            showGradeDialog(grade, preferencesRepository.gradeColorTheme)
            if (!grade.isRead) {
                grade.isRead = true
                updateItem(grade, position)
                getHeaderOfItem(grade.subject).let { header ->
                    (header.value as GradeDetailsHeader).newGrades--
                    updateHeaderItem(header)
                }
                newGradesAmount--
                updateMarkAsDoneButton()
                updateGrade(grade)
            }
        }
    }

    fun onMarkAsReadSelected(): Boolean {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semesters = semesterRepository.getSemesters(student)
            val semester = semesters.first { item -> item.semesterId == currentSemesterId }
            val unreadGrades = gradeRepository.getUnreadGrades(semester).first()

            Timber.i("Mark as read ${unreadGrades.size} grades")
            gradeRepository.updateGrades(unreadGrades.map { it.apply { isRead = true } })
        }
            .logResourceStatus("mark grades as read")
            .onResourceSuccess { loadData(currentSemesterId, false) }
            .onResourceError(errorHandler::dispatch)
            .launch("mark")
        return true
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade details")
        view?.notifyParentRefresh()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        view?.notifyParentRefresh()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) {
                if (preferencesRepository.gradeExpandMode != GradeExpandMode.ALWAYS_EXPANDED) collapseAllItems()
                scrollToStart()
            }
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        cancelJobs("load")
    }

    fun updateMarkAsDoneButton() {
        view?.enableMarkAsDoneButton(newGradesAmount > 0)
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            averageProvider.getGradesDetailsWithAverage(student, semesterId, forceRefresh)
        }
            .logResourceStatus("load grade details")
            .onResourceData {
                val gradeItems = createGradeItems(it)
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(gradeItems.isNotEmpty())
                    showEmpty(gradeItems.isEmpty())
                    updateNewGradesAmount(it)
                    updateMarkAsDoneButton()
                    updateData(
                        data = gradeItems,
                        expandMode = preferencesRepository.gradeExpandMode,
                        preferencesRepository.gradeColorTheme
                    )
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "grade_details",
                    "items" to it.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    enableSwipe(true)
                    showRefresh(false)
                    showProgress(false)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .catch {
                errorHandler.dispatch(it)
                view?.notifyParentDataLoaded(semesterId)
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun updateNewGradesAmount(grades: List<GradeSubject>) {
        newGradesAmount = grades.sumOf { item ->
            item.grades.sumOf { grade -> (if (!grade.isRead) 1 else 0).toInt() }
        }
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showProgress(false)
            } else showError(message, error)
        }
    }

    private fun createGradeItems(items: List<GradeSubject>): List<GradeDetailsItem> {
        return items
            .let { gradesWithAverages ->
                if (!preferencesRepository.showSubjectsWithoutGrades) {
                    gradesWithAverages.filter { it.grades.isNotEmpty() }
                } else gradesWithAverages
            }
            .let { gradeSubjects ->
                when (preferencesRepository.gradeSortingMode) {
                    DATE -> gradeSubjects.sortedByDescending { gradeDetailsWithAverage ->
                        gradeDetailsWithAverage.grades.maxByOrNull { it.date }?.date
                    }
                    ALPHABETIC -> gradeSubjects.sortedBy { gradeDetailsWithAverage ->
                        gradeDetailsWithAverage.subject.lowercase()
                    }
                    AVERAGE -> gradeSubjects.sortedByDescending { it.average }
                }
            }
            .map { (subject, average, points, _, grades) ->
                val subItems = grades
                    .sortedByDescending { it.date }
                    .map { GradeDetailsItem(it, ViewType.ITEM) }

                val gradeDetailsItems = listOf(
                    GradeDetailsItem(
                        GradeDetailsHeader(
                            subject = subject,
                            average = average,
                            pointsSum = points,
                            grades = subItems
                        ).apply {
                            newGrades = grades.filter { grade -> !grade.isRead }.size
                        }, ViewType.HEADER
                    )
                )

                if (preferencesRepository.gradeExpandMode == GradeExpandMode.ALWAYS_EXPANDED) {
                    gradeDetailsItems + subItems
                } else {
                    gradeDetailsItems
                }
            }.flatten()
    }

    private fun updateGrade(grade: Grade) {
        resourceFlow { gradeRepository.updateGrade(grade) }
            .logResourceStatus("update grade result ${grade.id}")
            .onResourceError(errorHandler::dispatch)
            .launch("update")
    }
}
