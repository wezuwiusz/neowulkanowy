package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LuckyNumberPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LuckyNumberView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: LuckyNumberView) {
        super.onAttachView(view)
        view.run {
            initView()
            showContent(false)
            enableSwipe(false)
        }
        Timber.i("Lucky number view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading lucky number started")
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMapMaybe { luckyNumberRepository.getLuckyNumber(it, forceRefresh) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                        enableSwipe(true)
                    }
                }
                .subscribe({
                    Timber.i("Loading lucky number result: Success")
                    view?.apply {
                        updateData(it)
                        showContent(true)
                        showEmpty(false)
                        showErrorView(false)
                    }
                    analytics.logEvent("load_lucky_number", "lucky_number" to it.luckyNumber, "force_refresh" to forceRefresh)
                }, {
                    Timber.i("Loading lucky number result: An exception occurred")
                    errorHandler.dispatch(it)
                }, {
                    Timber.i("Loading lucky number result: No lucky number found")
                    view?.run {
                        showContent(false)
                        showEmpty(true)
                        showEmpty(false)
                    }
                })
            )
        }
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
        Timber.i("Force refreshing the lucky number")
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
}
