package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import android.os.Build
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
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
    private val analytics: FirebaseAnalyticsHelper,
    private var appInfo: AppInfo
) : BasePresenter<MessagePreviewView>(errorHandler, studentRepository, schedulers) {

    var message: Message? = null

    var attachments: List<MessageAttachment>? = null

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
                    this@MessagePreviewPresenter.attachments = message.attachments
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

    fun onShare(): Boolean {
        message?.let {
            var text = "Temat: ${it.subject.ifBlank { view?.messageNoSubjectString.orEmpty() }}\n" + when (it.sender.isNotEmpty()) {
                true -> "Od: ${it.sender}\n"
                false -> "Do: ${it.recipient}\n"
            } + "Data: ${it.date.toFormattedString("yyyy-MM-dd HH:mm:ss")}\n\n${it.content}"

            attachments?.let { attachments ->
                if (attachments.isNotEmpty()) {
                    text += "\n\nZałączniki:"

                    attachments.forEach { attachment ->
                        text += "\n${attachment.filename}: ${attachment.url}"
                    }
                }
            }

            view?.shareText(text, "FW: ${it.subject.ifBlank { view?.messageNoSubjectString.orEmpty() }}")
            return true
        }
        return false
    }

    @SuppressLint("NewApi")
    fun onPrint(): Boolean {
        if (appInfo.systemVersion < Build.VERSION_CODES.LOLLIPOP) return false
        message?.let {
            val dateString = it.date.toFormattedString("yyyy-MM-dd HH:mm:ss")
            val infoContent = "<div><h4>Data wysłania</h4>$dateString</div>" + when {
                it.sender.isNotEmpty() -> "<div><h4>Od</h4>${it.sender}</div>"
                else -> "<div><h4>Do</h4>${it.recipient}</div>"
            }

            val messageContent = "<p>${it.content}</p>"
                .replace(Regex("[\\n\\r]{2,}"), "</p><p>")
                .replace(Regex("[\\n\\r]"), "<br>")

            val jobName = "Wiadomość " + when {
                it.sender.isNotEmpty() -> "od ${it.sender}"
                else -> "do ${it.recipient}"
            } + " $dateString: ${it.subject.ifBlank { view?.messageNoSubjectString.orEmpty() }} | Wulkanowy"

            view?.apply {
                val html = printHTML
                    .replace("%SUBJECT%", it.subject.ifBlank { view?.messageNoSubjectString.orEmpty() })
                    .replace("%CONTENT%", messageContent)
                    .replace("%INFO%", infoContent)
                printDocument(html, jobName)
            }
            return true
        }
        return false
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
