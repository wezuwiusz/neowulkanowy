package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceNotLoading
import io.github.wulkanowy.data.pojos.MessageDraft
import io.github.wulkanowy.data.repositories.*
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val mailboxRepository: MailboxRepository,
    private val recipientRepository: RecipientRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<SendMessageView>(errorHandler, studentRepository) {

    private val messageUpdateChannel = Channel<Unit>()

    fun onAttachView(view: SendMessageView, reason: String?, message: Message?, reply: Boolean?) {
        super.onAttachView(view)
        view.initView()
        initializeSubjectStream()
        Timber.i("Send message view was initialized")
        loadData(message, reply)
        with(view) {
            if (messageRepository.draftMessage != null && reply == null) {
                view.showMessageBackupDialog()
            }
            reason?.let {
                setSubject("Usprawiedliwenie")
                setContent(it)
            }
            message?.let {
                setSubject(
                    when (reply) {
                        true -> "RE: "
                        else -> "FW: "
                    } + message.subject
                )
                if (preferencesRepository.fillMessageContent || reply != true) {
                    setContent(buildString {
                        if (reply == true) {
                            append("<br><br>")
                        }

                        append("Od: ${message.sender}<br>")
                        append("Do: ${message.recipients}<br>")
                        append("Data: ${message.date.toFormattedString("yyyy-MM-dd HH:mm:ss")}<br><br>")
                        append(message.content)
                    })
                }
            }
        }
    }

    fun onTouchScroll(): Boolean {
        return view?.run {
            if (isDropdownListVisible) {
                hideDropdownList()
                true
            } else false
        } == true
    }

    fun onRecipientsTextChange(text: String) {
        if (text.isBlank()) return
        view?.scrollToRecipients()
    }

    fun onUpNavigate(): Boolean {
        view?.popView()
        return true
    }

    fun onSend(): Boolean {
        view?.run {
            when {
                formRecipientsData.isEmpty() -> showMessage(messageRequiredRecipients)
                formContentValue.length < 3 -> showMessage(messageContentMinLength)
                else -> {
                    sendMessage(
                        subject = formSubjectValue,
                        content = formContentValue,
                        recipients = formRecipientsData.map { it.recipient }
                    )
                    return true
                }
            }
        }
        return false
    }

    private fun loadData(message: Message?, reply: Boolean?) {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val mailbox = mailboxRepository.getMailbox(student)

            Timber.i("Loading recipients started")
            val recipients = createChips(
                recipients = recipientRepository.getRecipients(
                    student = student,
                    mailbox = mailbox,
                    type = MailboxType.EMPLOYEE,
                )
            )
            Timber.i("Loading recipients result: Success, fetched %d recipients", recipients.size)

            Timber.i("Loading message recipients started")
            val messageRecipients = when {
                message != null && reply == true -> recipientRepository.getMessageSender(
                    student = student,
                    message = message,
                    mailbox = mailbox,
                )
                else -> emptyList()
            }.let { createChips(it) }
            Timber.i(
                "Loaded message recipients to reply result: Success, fetched %d recipients",
                messageRecipients.size
            )

            Triple(mailbox, recipients, messageRecipients)
        }
            .logResourceStatus("load recipients")
            .onEach {
                when (it) {
                    is Resource.Loading -> view?.run {
                        showProgress(true)
                        showContent(false)
                    }
                    is Resource.Success -> it.data.let { (mailbox, recipientChips, selectedRecipientChips) ->
                        view?.run {
                            setMailbox(getMailboxName(mailbox))
                            setRecipients(recipientChips)
                            if (selectedRecipientChips.isNotEmpty()) setSelectedRecipients(
                                selectedRecipientChips
                            )
                            showContent(true)
                        }
                    }
                    is Resource.Error -> {
                        view?.showContent(true)
                        errorHandler.dispatch(it.error)
                    }
                }
            }.onResourceNotLoading {
                view?.run { showProgress(false) }
            }.launch()
    }

    private fun sendMessage(subject: String, content: String, recipients: List<Recipient>) {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val mailbox = mailboxRepository.getMailbox(student)
            messageRepository.sendMessage(
                student = student,
                subject = subject,
                content = content,
                recipients = recipients,
                mailboxId = mailbox.globalKey,
            )
        }.logResourceStatus("sending message").onEach {
            when (it) {
                is Resource.Loading -> view?.run {
                    showSoftInput(false)
                    showContent(false)
                    showProgress(true)
                    showActionBar(false)
                }
                is Resource.Success -> {
                    view?.clearDraft()
                    view?.run {
                        showMessage(messageSuccess)
                        popView()
                    }
                    analytics.logEvent("send_message", "recipients" to recipients.size)
                }
                is Resource.Error -> {
                    view?.run {
                        showContent(true)
                        showProgress(false)
                        showActionBar(true)
                    }
                    errorHandler.dispatch(it.error)
                }
            }
        }.launch("send")
    }

    private fun createChips(recipients: List<Recipient>): List<RecipientChipItem> {
        return recipients.map {
            RecipientChipItem(
                title = it.userName,
                summary = buildString {
                    getMailboxType(it.type)?.let(::append)
                    if (isNotBlank()) append(" ")

                    append("(${it.schoolShortName})")
                },
                recipient = it
            )
        }
    }

    private fun getMailboxName(mailbox: Mailbox): String {
        return buildString {
            append(mailbox.userName)
            append(" - ")
            append(getMailboxType(mailbox.type))

            if (mailbox.type == MailboxType.PARENT) {
                append(" - ")
                append(mailbox.studentName)
            }

            append(" - ")
            append("(${mailbox.schoolNameShort})")
        }
    }

    private fun getMailboxType(type: MailboxType): String? = when (type) {
        MailboxType.STUDENT -> view?.mailboxStudent
        MailboxType.PARENT -> view?.mailboxParent
        MailboxType.GUARDIAN -> view?.mailboxGuardian
        MailboxType.EMPLOYEE -> view?.mailboxEmployee
        MailboxType.UNKNOWN -> null
    }

    fun onMessageContentChange() {
        presenterScope.launch {
            messageUpdateChannel.send(Unit)
        }
    }

    @OptIn(FlowPreview::class)
    private fun initializeSubjectStream() {
        presenterScope.launch {
            messageUpdateChannel.consumeAsFlow()
                .debounce(250)
                .catch { Timber.e(it) }
                .collect {
                    saveDraftMessage()
                    Timber.i("Draft message was saved!")
                }
        }
    }

    private fun saveDraftMessage() {
        messageRepository.draftMessage = MessageDraft(
            view?.formRecipientsData!!,
            view?.formSubjectValue!!,
            view?.formContentValue!!
        )
    }

    fun restoreMessageParts() {
        val draftMessage = messageRepository.draftMessage ?: return
        view?.setSelectedRecipients(draftMessage.recipients)
        view?.setSubject(draftMessage.subject)
        view?.setContent(draftMessage.content)
        Timber.i("Continue work on draft")
    }

    fun getRecipientsNames(): String {
        return messageRepository.draftMessage?.recipients.orEmpty()
            .joinToString { it.recipient.userName }
    }

    fun clearDraft() {
        messageRepository.draftMessage = null
        Timber.i("Draft cleared!")
    }

    fun getMessageBackupContent(recipients: String) =
        if (recipients.isEmpty()) view?.getMessageBackupDialogString()
        else view?.getMessageBackupDialogStringWithRecipients(recipients)
}
