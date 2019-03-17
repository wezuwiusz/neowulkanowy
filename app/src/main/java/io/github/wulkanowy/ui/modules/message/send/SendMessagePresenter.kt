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
import timber.log.Timber
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<SendMessageView>(errorHandler) {

    fun onAttachView(view: SendMessageView, message: Message?) {
        super.onAttachView(view)
        Timber.i("Send message view is attached")
        loadData(message)
        view.apply {
            message?.let {
                setSubject("RE: ${message.subject}")
                if (preferencesRepository.fillMessageContent) {
                    setContent(when (message.sender.isNotEmpty()) {
                        true -> "\n\nOd: ${message.sender}\n"
                        false -> "\n\nDo: ${message.recipient}\n"
                    } + "Data: ${message.date.toFormattedString("yyyy-MM-dd HH:mm:ss")}\n\n${message.content}")
                }
            }
        }
    }

    private fun loadData(message: Message?) {
        var reportingUnit: ReportingUnit? = null
        var recipients: List<Recipient> = emptyList()
        var selectedRecipient: List<Recipient> = emptyList()

        Timber.i("Loading recipients started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it).map { semester -> it to semester } }
            .flatMapCompletable { (student, semester) ->
                reportingUnitRepository.getReportingUnit(student, semester.unitId)
                    .doOnSuccess { reportingUnit = it }
                    .flatMap { recipientRepository.getRecipients(student, 2, it).toMaybe() }
                    .doOnSuccess {
                        Timber.i("Loading recipients result: Success, fetched %d recipients", it.size)
                        recipients = it
                    }
                    .flatMapCompletable {
                        if (message == null) Completable.complete()
                        else recipientRepository.getMessageRecipients(student, message)
                            .doOnSuccess {
                                Timber.i("Loaded message recipients to reply result: Success, fetched %d recipients", it.size)
                                selectedRecipient = it
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
                view?.apply {
                    if (reportingUnit !== null) {
                        reportingUnit?.let { setReportingUnit(it) }
                        setRecipients(recipients)
                        if (selectedRecipient.isNotEmpty()) setSelectedRecipients(selectedRecipient)
                        showContent(true)
                    } else {
                        Timber.e("Loading recipients result: Can't find the reporting unit")
                        view?.showEmpty(true)
                    }
                }
            }, {
                Timber.e("Loading recipients result: An exception occurred")
                view?.showContent(true)
                errorHandler.dispatch(it)
            }))
    }

    private fun sendMessage(subject: String, content: String, recipients: List<Recipient>) {
        Timber.i("Sending message started")
        disposable.add(messageRepository.sendMessage(subject, content, recipients)
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

    fun onSend(): Boolean {
        view?.run {
            when {
                formRecipientsData.isEmpty() -> showMessage(messageRequiredRecipients)
                formContentValue.length < 3 -> showMessage(messageContentMinLength)
                else -> {
                    sendMessage(
                        subject = formSubjectValue,
                        content = formContentValue,
                        recipients = formRecipientsData
                    )
                    return true
                }
            }
        }
        return false
    }
}
