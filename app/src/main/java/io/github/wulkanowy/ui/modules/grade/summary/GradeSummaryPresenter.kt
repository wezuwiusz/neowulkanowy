package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.enums.GradeSortingMode
import io.github.wulkanowy.data.enums.GradeSortingMode.*
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.ui.modules.grade.GradeSubject
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val averageProvider: GradeAverageProvider,
    private val analytics: AnalyticsHelper
) : BasePresenter<GradeSummaryView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeSummaryView) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade summary data started")

        loadData(semesterId, forceRefresh)
        if (!forceRefresh) view?.showErrorView(false)
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            averageProvider.getGradesDetailsWithAverage(student, semesterId, forceRefresh)
        }
            .logResourceStatus("load grade summary", showData = true)
            .mapResourceData { createGradeSummaryItems(it) }
            .onResourceData {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "grade_summary",
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
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade summary")
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
            if (!isViewEmpty) resetView()
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

    fun onCalculatedAverageHelpClick() {
        view?.showCalculatedAverageHelpDialog()
    }

    fun onFinalAverageHelpClick() {
        view?.showFinalAverageHelpDialog()
    }

    private fun createGradeSummaryItems(items: List<GradeSubject>): List<GradeSummary> {
        return items
            .filter { !checkEmpty(it) }
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
            .map { it.summary.copy(average = it.average) }
    }

    private fun checkEmpty(gradeSummary: GradeSubject): Boolean {
        return gradeSummary.run {
            summary.finalGrade.isBlank()
                    && summary.predictedGrade.isBlank()
                    && average == .0
                    && points.isBlank()
        }
    }
}
