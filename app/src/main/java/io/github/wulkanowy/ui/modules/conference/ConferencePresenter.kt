package io.github.wulkanowy.ui.modules.conference

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
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
        Timber.i("Loading conference data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            conferenceRepository.getConferences(student, semester, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    if (!it.data.isNullOrEmpty()) {
                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showContent(true)
                            updateData(it.data.sortedByDescending { conference -> conference.date })
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading conference result: Success")
                    view?.run {
                        updateData(it.data!!.sortedByDescending { conference -> conference.date })
                        showContent(it.data.isNotEmpty())
                        showEmpty(it.data.isEmpty())
                        showErrorView(false)
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "conferences",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading conference result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
            }
        }.launch()
    }
}
