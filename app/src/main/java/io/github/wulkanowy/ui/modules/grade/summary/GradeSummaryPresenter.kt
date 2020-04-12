package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calcAverage
import timber.log.Timber
import java.lang.String.format
import java.util.Locale.FRANCE
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeSummaryRepository: GradeSummaryRepository,
    private val semesterRepository: SemesterRepository,
    private val averageProvider: GradeAverageProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<GradeSummaryView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeSummaryView) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade summary data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it).map { semesters -> it to semesters } }
            .flatMap { (student, semesters) ->
                gradeSummaryRepository.getGradesSummary(student, semesters.first { it.semesterId == semesterId }, forceRefresh)
                    .map { it.sortedBy { subject -> subject.subject } }
                    .flatMap { gradesSummary ->
                        averageProvider.getGradeAverage(student, semesters, semesterId, forceRefresh)
                            .map { averages -> createGradeSummaryItemsAndHeader(gradesSummary, averages) }
                    }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded(semesterId)
                }
            }.subscribe({ (gradeSummaryItems, gradeSummaryHeader) ->
                Timber.i("Loading grade summary result: Success")
                view?.run {
                    showEmpty(gradeSummaryItems.isEmpty())
                    showContent(gradeSummaryItems.isNotEmpty())
                    showErrorView(false)
                    updateData(gradeSummaryItems, gradeSummaryHeader)
                }
                analytics.logEvent("load_grade_summary", "items" to gradeSummaryItems.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading grade summary result: An exception occurred")
                errorHandler.dispatch(it)
            })
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
        disposable.clear()
    }

    private fun createGradeSummaryItemsAndHeader(gradesSummary: List<GradeSummary>, averages: List<Triple<String, Double, String>>): Pair<List<GradeSummaryItem>, GradeSummaryScrollableHeader> {
        return averages.filter { value -> value.second != 0.0 }
            .let { filteredAverages ->
                gradesSummary.filter { !checkEmpty(it, filteredAverages) }
                    .map { gradeSummary ->
                        GradeSummaryItem(
                            summary = gradeSummary,
                            average = formatAverage(filteredAverages.singleOrNull { gradeSummary.subject == it.first }?.second ?: .0, "")
                        )
                    }.let {
                        it to GradeSummaryScrollableHeader(
                            formatAverage(gradesSummary.calcAverage()),
                            formatAverage(filteredAverages.map { values -> values.second }.average()))
                    }
            }
    }

    private fun checkEmpty(gradeSummary: GradeSummary, averages: List<Triple<String, Double, String>>): Boolean {
        return gradeSummary.run {
            finalGrade.isBlank() && predictedGrade.isBlank() && averages.singleOrNull { it.first == subject } == null
        }
    }

    private fun formatAverage(average: Double, defaultValue: String = "-- --"): String {
        return if (average == 0.0) defaultValue
        else format(FRANCE, "%.2f", average)
    }
}
