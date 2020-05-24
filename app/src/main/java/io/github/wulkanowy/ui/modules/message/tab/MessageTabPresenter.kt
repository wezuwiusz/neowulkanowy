package io.github.wulkanowy.ui.modules.message.tab

import android.annotation.SuppressLint
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
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

    private var lastSearchQuery = ""

    private var messages = emptyList<Message>()

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

    fun onMessageItemSelected(message: Message, position: Int) {
        Timber.i("Select message ${message.id} item (position: $position)")
        view?.run {
            openMessage(message)
            if (message.unread) {
                message.unread = false
                updateItem(message, position)
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
                    messages = it
                    onSearchQueryTextChange(lastSearchQuery)
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

    @SuppressLint("DefaultLocale")
    fun onSearchQueryTextChange(query: String) {
        lastSearchQuery = query

        val lowerCaseQuery = query.toLowerCase()
        val filteredList = mutableListOf<Message>()
        messages.forEach {
            if (lowerCaseQuery in it.subject.toLowerCase() ||
                lowerCaseQuery in it.sender.toLowerCase() ||
                lowerCaseQuery in it.recipient.toLowerCase() ||
                lowerCaseQuery in it.date.toFormattedString()
            ) {
                filteredList.add(it)
            }
        }

        Timber.d("Applying filter. Full list: ${messages.size}, filtered: ${filteredList.size}")

        updateData(filteredList)
    }

    private fun updateData(data: List<Message>) {
        view?.run {
            showEmpty(data.isEmpty())
            showContent(data.isNotEmpty())
            showErrorView(false)
            updateData(data)
            resetListPosition()
        }
    }
}
