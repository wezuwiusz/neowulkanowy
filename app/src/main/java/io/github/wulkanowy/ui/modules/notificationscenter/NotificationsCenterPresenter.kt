package io.github.wulkanowy.ui.modules.notificationscenter

import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class NotificationsCenterPresenter @Inject constructor(
    private val notificationRepository: NotificationRepository,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<NotificationsCenterView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: NotificationsCenterView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Notifications centre view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData() {
        Timber.i("Loading notifications data started")

        flow {
            val studentId = studentRepository.getCurrentStudent(false).id
            emitAll(notificationRepository.getNotifications(studentId))
        }
            .map { notificationList -> notificationList.sortedByDescending { it.date } }
            .catch { Timber.i("Loading notifications result: An exception occurred: `$it`") }
            .onEach {
                Timber.i("Loading notifications result: Success")

                if (it.isEmpty()) {
                    view?.run {
                        showContent(false)
                        showProgress(false)
                        showEmpty(true)
                    }
                } else {
                    view?.run {
                        showContent(true)
                        showProgress(false)
                        showEmpty(false)
                        updateData(it)
                    }
                }
            }
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
}