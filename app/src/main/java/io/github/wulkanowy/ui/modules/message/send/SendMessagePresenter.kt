package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxMaybe
import kotlinx.coroutines.rx2.rxSingle
import timber.log.Timber
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<SendMessageView>(errorHandler, studentRepository, schedulers) {

    fun onAttachView(view: SendMessageView, message: Message?, reply: Boolean?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Send message view was initialized")
        loadData(message, reply)
        view.apply {
            message?.let {
                setSubject(when (reply) {
                    true -> "RE: "
                    else -> "FW: "
                } + message.subject)
                if (preferencesRepository.fillMessageContent || reply != true) {
                    setContent(
                        when (reply) {
                            true -> "\n\n"
                            else -> ""
                        } + when (message.sender.isNotEmpty()) {
                            true -> "Od: ${message.sender}\n"
                            false -> "Do: ${message.recipient}\n"
                        } + "Data: ${message.date.toFormattedString("yyyy-MM-dd HH:mm:ss")}\n\n${message.content}")
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
        var reportingUnit: ReportingUnit? = null
        var recipientChips: List<RecipientChipItem> = emptyList()
        var selectedRecipientChips: List<RecipientChipItem> = emptyList()

        Timber.i("Loading recipients started")
        disposable.add(rxSingle { studentRepository.getCurrentStudent() }
            .flatMap { rxSingle { semesterRepository.getCurrentSemester(it) }.map { semester -> it to semester } }
            .flatMapCompletable { (student, semester) ->
                rxMaybe { reportingUnitRepository.getReportingUnit(student, semester.unitId) }
                    .doOnSuccess { reportingUnit = it }
                    .flatMap { rxMaybe { recipientRepository.getRecipients(student, 2, it) } }
                    .doOnSuccess {
                        Timber.i("Loading recipients result: Success, fetched %d recipients", it.size)
                        recipientChips = createChips(it)
                    }
                    .flatMapCompletable {
                        if (message == null || reply != true) Completable.complete()
                        else rxSingle { recipientRepository.getMessageRecipients(student, message) }
                            .doOnSuccess {
                                Timber.i("Loaded message recipients to reply result: Success, fetched %d recipients", it.size)
                                selectedRecipientChips = createChips(it)
                            }
                            .ignoreElement()
                    }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    showProgress(true)
                    showContent(false)
                }
            }
            .doFinally { view?.run { showProgress(false) } }
            .subscribe({
                view?.run {
                    if (reportingUnit !== null) {
                        reportingUnit?.let { setReportingUnit(it) }
                        setRecipients(recipientChips)
                        if (selectedRecipientChips.isNotEmpty()) setSelectedRecipients(selectedRecipientChips)
                        showContent(true)
                    } else {
                        Timber.i("Loading recipients result: Can't find the reporting unit")
                        view?.showEmpty(true)
                    }
                }
            }, {
                Timber.i("Loading recipients result: An exception occurred")
                view?.showContent(true)
                errorHandler.dispatch(it)
            }))
    }

    private fun sendMessage(subject: String, content: String, recipients: List<Recipient>) {
        Timber.i("Sending message started")
        disposable.add(rxSingle { studentRepository.getCurrentStudent() }
            .flatMap { rxSingle { messageRepository.sendMessage(it, subject, content, recipients) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    showSoftInput(false)
                    showContent(false)
                    showProgress(true)
                    showActionBar(false)
                }
            }
            .subscribe({
                Timber.i("Sending message result: Success")
                analytics.logEvent("send_message", "recipients" to recipients.size)
                view?.run {
                    showMessage(messageSuccess)
                    popView()
                }
            }, {
                Timber.i("Sending message result: An exception occurred")
                view?.run {
                    showContent(true)
                    showProgress(false)
                    showActionBar(true)
                }
                errorHandler.dispatch(it)
            })
        )
    }

    private fun createChips(recipients: List<Recipient>): List<RecipientChipItem> {
        fun generateCorrectSummary(recipientRealName: String): String {
            val substring = recipientRealName.substringBeforeLast("-")
            return when {
                substring == recipientRealName -> recipientRealName
                substring.indexOf("(") != -1 -> {
                    recipientRealName.indexOf("(")
                        .let { recipientRealName.substring(if (it != -1) it else 0) }
                }
                substring.indexOf("[") != -1 -> {
                    recipientRealName.indexOf("[")
                        .let { recipientRealName.substring(if (it != -1) it else 0) }
                }
                else -> recipientRealName.substringAfter("-")
            }.trim()
        }

        return recipients.map {
            RecipientChipItem(
                title = it.name,
                summary = generateCorrectSummary(it.realName),
                recipient = it
            )
        }
    }
}
