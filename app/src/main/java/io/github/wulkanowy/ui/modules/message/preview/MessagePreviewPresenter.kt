package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import androidx.core.text.parseAsHtml
import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<MessagePreviewView>(errorHandler, studentRepository) {

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

    private fun loadData(messageToLoad: Message) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            messageRepository.getMessage(student, messageToLoad, true)
        }
            .logResourceStatus("message ${messageToLoad.messageId} preview")
            .onResourceData {
                if (it != null) {
                    message = it.message
                    attachments = it.attachments
                    view?.apply {
                        setMessageWithAttachment(it)
                        showContent(true)
                        initOptions()
                    }
                } else {
                    view?.run {
                        showMessage(messageNotExists)
                        popView()
                    }
                }
            }
            .onResourceSuccess {
                if (it != null) {
                    analytics.logEvent(
                        "load_item",
                        "type" to "message_preview",
                        "length" to it.message.content.length
                    )
                }
            }
            .onResourceNotLoading { view?.showProgress(false) }
            .onResourceError {
                retryCallback = { onMessageLoadRetry(messageToLoad) }
                errorHandler.dispatch(it)
            }
            .launch()
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
        val message = message ?: return false
        val subject = message.subject.ifBlank { view?.messageNoSubjectString.orEmpty() }

        val text = buildString {
            appendLine("Temat: $subject")
            appendLine("Od: ${message.sender}")
            appendLine("Do: ${message.recipients}")
            appendLine("Data: ${message.date.toFormattedString("yyyy-MM-dd HH:mm:ss")}")

            appendLine()

            appendLine(message.content.parseAsHtml())

            if (!attachments.isNullOrEmpty()) {
                appendLine()
                appendLine("Załączniki:")

                append(attachments.orEmpty().joinToString(separator = "\n") { attachment ->
                    "${attachment.filename}: ${attachment.url}"
                })
            }
        }

        view?.shareText(
            subject = "FW: $subject",
            text = text,
        )
        return true
    }

    @SuppressLint("NewApi")
    fun onPrint(): Boolean {
        val message = message ?: return false
        val subject = message.subject.ifBlank { view?.messageNoSubjectString.orEmpty() }

        val dateString = message.date.toFormattedString("yyyy-MM-dd HH:mm:ss")

        val infoContent = buildString {
            append("<div><h4>Data wysłania</h4>$dateString</div>")

            append("<div><h4>Od</h4>${message.sender}</div>")
            append("<div><h4>DO</h4>${message.recipients}</div>")
        }
        val messageContent = "<p>${message.content}</p>"
            .replace(Regex("[\\n\\r]{2,}"), "</p><p>")
            .replace(Regex("[\\n\\r]"), "<br>")

        val jobName = buildString {
            append("Wiadomość ")
            append("od ${message.correspondents}")
            append("do ${message.correspondents}")
            append(" $dateString: $subject | Wulkanowy")
        }

        view?.apply {
            val html = printHTML
                .replace("%SUBJECT%", subject)
                .replace("%CONTENT%", messageContent)
                .replace("%INFO%", infoContent)
            printDocument(html, jobName)
        }

        return true
    }

    private fun deleteMessage() {
        message ?: return

        view?.run {
            showContent(false)
            showProgress(true)
            showOptions(show = false, isReplayable = false)
            showErrorView(false)
        }

        Timber.i("Delete message ${message?.messageGlobalKey}")

        presenterScope.launch {
            runCatching {
                val student = studentRepository.getCurrentStudent(decryptPass = true)
                val mailbox = messageRepository.getMailboxByStudent(student)
                messageRepository.deleteMessage(student, mailbox!!, message!!)
            }
                .onFailure {
                    retryCallback = { onMessageDelete() }
                    errorHandler.dispatch(it)
                }
                .onSuccess {
                    view?.run {
                        showMessage(deleteMessageSuccessString)
                        popView()
                    }
                }

            view?.showProgress(false)
        }
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showContent(false)
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
            showOptions(
                show = message != null,
                isReplayable = message?.folderId != MessageFolder.SENT.id,
            )
            message?.let {
                when (it.folderId == MessageFolder.TRASHED.id) {
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
