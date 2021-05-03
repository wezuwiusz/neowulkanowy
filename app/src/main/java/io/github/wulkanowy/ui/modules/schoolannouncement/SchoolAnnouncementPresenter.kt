package io.github.wulkanowy.ui.modules.schoolannouncement

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SchoolAnnouncementPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: AnalyticsHelper,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
) : BasePresenter<SchoolAnnouncementView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: SchoolAnnouncementView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("School announcement view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the School announcement")
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

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading School announcement data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    if (!it.data.isNullOrEmpty()) {
                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showContent(true)
                            updateData(it.data)
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading School announcement result: Success")
                    view?.apply {
                        updateData(it.data!!.sortedByDescending { item -> item.date })
                        showEmpty(it.data.isEmpty())
                        showErrorView(false)
                        showContent(it.data.isNotEmpty())
                    }
                    analytics.logEvent(
                        "load_school_announcement",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading School announcement result: An exception occurred")
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
}
