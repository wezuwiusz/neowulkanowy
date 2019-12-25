package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MessagePreviewView>(errorHandler, studentRepository, schedulers) {

    var messageId = 0L

    private var message: Message? = null

    private lateinit var lastError: Throwable

    private var retryCallback: () -> Unit = {}

    fun onAttachView(view: MessagePreviewView, id: Long) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(id)
    }

    private fun onMessageLoadRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(messageId)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(id: Long) {
        Timber.i("Loading message $id preview started")
        messageId = id
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { messageRepository.getMessage(it, messageId, true) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally { view?.showProgress(false) }
                .subscribe({ message ->
                    Timber.i("Loading message $id preview result: Success ")
                    this@MessagePreviewPresenter.message = message
                    view?.run {
                        message.let {
                            setSubject(if (it.subject.isNotBlank()) it.subject else noSubjectString)
                            setDate(it.date.toFormattedString("yyyy-MM-dd HH:mm:ss"))
                            setContent(it.content)
                            initOptions()

                            if (it.folderId == MessageFolder.SENT.id) setRecipient(it.recipient)
                            else setSender(it.sender)
                        }
                    }
                    analytics.logEvent("load_message_preview", "length" to message.content.length)
                }) {
                    Timber.i("Loading message $id preview result: An exception occurred ")
                    retryCallback = { onMessageLoadRetry() }
                    errorHandler.dispatch(it)
                })
        }
    }

    fun onReply(): Boolean {
        return if (message != null) {
            view?.openMessageReply(message)
            true
        } else false
    }

    fun onForward(): Boolean {
        return if (message != null) {
            view?.openMessageForward(message)
            true
        } else false
    }

    private fun deleteMessage() {
        message?.let { message ->
            disposable.add(messageRepository.deleteMessage(message)
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.run {
                        showContent(false)
                        showProgress(true)
                        showOptions(false)
                        showErrorView(false)
                    }
                }
                .doFinally {
                    view?.showProgress(false)
                }
                .subscribe({
                    view?.run {
                        notifyParentMessageDeleted(message)
                        showMessage(deleteMessageSuccessString)
                        popView()
                    }
                }, { error ->
                    retryCallback = { onMessageDelete() }
                    errorHandler.dispatch(error)
                }, {
                    view?.showErrorView(true)
                })
            )
        }
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showErrorView(true)
            setErrorRetryCallback { retryCallback() }
        }
    }

    fun onMessageDelete(): Boolean {
        deleteMessage()
        return true
    }

    private fun initOptions() {
        view?.apply {
            showOptions(message != null)
            message?.let {
                when (it.removed) {
                    true -> setDeletedOptionsLabels()
                    false -> setNotDeletedOptionsLabels()
                }
            }

        }
    }

    fun onCreateOptionsMenu() {
        initOptions()
    }
}
