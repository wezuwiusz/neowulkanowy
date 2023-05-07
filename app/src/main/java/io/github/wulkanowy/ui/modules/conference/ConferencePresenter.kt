package io.github.wulkanowy.ui.modules.conference

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class ConferencePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val conferenceRepository: ConferenceRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<ConferenceView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: ConferenceView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Conferences view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onItemSelected(conference: Conference) {
        view?.openConferenceDialog(conference)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
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

    private fun loadData(forceRefresh: Boolean = false) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            conferenceRepository.getConferences(student, semester, forceRefresh)
        }
            .logResourceStatus("load conference data")
            .mapResourceData { it.sortedByDescending { conference -> conference.date } }
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
                    "type" to "conferences",
                    "items" to it.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showRefresh(false)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    fun onFragmentReselected() {
        Timber.i("Conference is reselected")
        if (view?.isViewEmpty == false) {
            view?.resetView()
        }
    }
}
