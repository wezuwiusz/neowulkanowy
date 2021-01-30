package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.ui.modules.grade.GradeSubject
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
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
        Timber.i("Loading grade summary started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            averageProvider.getGradesDetailsWithAverage(student, semesterId, forceRefresh)
        }.onEach {
            Timber.d("Loading grade summary status: ${it.status}, data: ${it.data != null}")
            when (it.status) {
                Status.LOADING -> {
                    val items = createGradeSummaryItems(it.data.orEmpty())
                    if (items.isNotEmpty()) {
                        Timber.i("Loading grade summary result: load cached data")
                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showEmpty(false)
                            showContent(true)
                            updateData(items)
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading grade summary result: Success")
                    val items = createGradeSummaryItems(it.data!!)
                    view?.run {
                        showEmpty(items.isEmpty())
                        showContent(items.isNotEmpty())
                        showErrorView(false)
                        updateData(items)
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "grade_summary",
                        "items" to it.data.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading grade summary result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
                notifyParentDataLoaded(semesterId)
            }
        }.launch()
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

    private fun createGradeSummaryItems(items: List<GradeSubject>): List<GradeSummary> {
        return items
            .filter { !checkEmpty(it) }
            .sortedBy { it.subject }
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
