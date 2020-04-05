package io.github.wulkanowy.ui.modules.message.tab

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MessageTabPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MessageTabView>(errorHandler, studentRepository, schedulers) {

    lateinit var folder: MessageFolder

    private lateinit var lastError: Throwable

    fun onAttachView(view: MessageTabView, folder: MessageFolder) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        this.folder = folder
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the $folder message")
        onParentViewLoadData(true)
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

    fun onDeleteMessage() {
        loadData(false)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    fun onMessageItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is MessageItem) {
            Timber.i("Select message ${item.message.id} item")
            view?.run {
                openMessage(item.message)
                if (item.message.unread) {
                    item.message.unread = false
                    updateItem(item)
                }
            }
        }
    }

    private fun loadData(forceRefresh: Boolean) {
        Timber.i("Loading $folder message data started")
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { student ->
                    semesterRepository.getCurrentSemester(student)
                        .flatMap { messageRepository.getMessages(student, it, folder, forceRefresh) }
                        .map { items -> items.map { MessageItem(it, view?.noSubjectString.orEmpty()) } }
                }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        enableSwipe(true)
                        notifyParentDataLoaded()
                    }
                }
                .subscribe({
                    Timber.i("Loading $folder message result: Success")
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        showErrorView(false)
                        updateData(it)
                    }
                    analytics.logEvent("load_messages", "items" to it.size, "folder" to folder.name)
                }) {
                    Timber.i("Loading $folder message result: An exception occurred")
                    errorHandler.dispatch(it)
                })
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
}
