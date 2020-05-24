package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MessagePreviewView>(errorHandler, studentRepository, schedulers) {

    var message: Message? = null

    private lateinit var lastError: Throwable

    private var retryCallback: () -> Unit = {}

    fun onAttachView(view: MessagePreviewView, message: Message?) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        this.message = message
        loadData(requireNotNull(message))
    }

    private fun onMessageLoadRetry(message: Message) {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(message)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(message: Message) {
        Timber.i("Loading message ${message.messageId} preview started")
        disposable.apply {
            clear()
            add(studentRepository.getStudentById(message.studentId)
                .flatMap { messageRepository.getMessage(it, message, true) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally { view?.showProgress(false) }
                .subscribe({ message ->
                    Timber.i("Loading message ${message.message.messageId} preview result: Success ")
                    this@MessagePreviewPresenter.message = message.message
                    view?.apply {
                        setMessageWithAttachment(message)
                        initOptions()
                    }
                    analytics.logEvent(
                        "load_item",
                        "type" to "message_preview",
                        "length" to message.message.content.length
                    )
                }) {
                    Timber.i("Loading message ${message.messageId} preview result: An exception occurred ")
                    retryCallback = { onMessageLoadRetry(message) }
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
            disposable.add(studentRepository.getCurrentStudent()
                .flatMap { messageRepository.deleteMessage(it, message) }
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
