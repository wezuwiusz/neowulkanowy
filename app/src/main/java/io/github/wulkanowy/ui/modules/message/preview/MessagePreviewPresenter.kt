package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import android.os.Build
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val analytics: AnalyticsHelper,
    private var appInfo: AppInfo
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

    private fun loadData(message: Message) {
        flowWithResourceIn {
            val student = studentRepository.getStudentById(message.studentId)
            messageRepository.getMessage(student, message, true)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading message ${message.messageId} preview started")
                Status.SUCCESS -> {
                    Timber.i("Loading message ${message.messageId} preview result: Success ")
                    if (it.data != null) {
                        this@MessagePreviewPresenter.message = it.data.message
                        this@MessagePreviewPresenter.attachments = it.data.attachments
                        view?.apply {
                            setMessageWithAttachment(it.data)
                            initOptions()
                        }
                        analytics.logEvent(
                            "load_item",
                            "type" to "message_preview",
                            "length" to it.data.message.content.length
                        )
                    } else {
                        view?.run {
                            showMessage(messageNotExists)
                            popView()
                        }
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading message ${message.messageId} preview result: An exception occurred ")
                    retryCallback = { onMessageLoadRetry(message) }
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.showProgress(false)
        }.launch()
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
        message ?: return

        view?.run {
            showContent(false)
            showProgress(true)
            showOptions(false)
            showErrorView(false)
        }

        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            messageRepository.deleteMessage(student, message!!)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Message ${message?.id} delete started")
                Status.SUCCESS -> {
                    Timber.d("Message ${message?.id} delete success")
                    view?.run {
                        showMessage(deleteMessageSuccessString)
                        popView()
                    }
                }
                Status.ERROR -> {
                    Timber.d("Message ${message?.id} delete failed")
                    retryCallback = { onMessageDelete() }
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.showProgress(false)
        }.launch("delete")
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
            showOptions(message != null)
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
