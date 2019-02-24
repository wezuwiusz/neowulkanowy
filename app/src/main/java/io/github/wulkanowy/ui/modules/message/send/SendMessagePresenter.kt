package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<SendMessageView>(errorHandler) {

    private lateinit var reportingUnit: ReportingUnit

    override fun onAttachView(view: SendMessageView) {
        Timber.i("Send message view is attached")
        super.onAttachView(view)
        view.run {
            initView()
            showBottomNav(false)
        }
        loadRecipients()
    }

    private fun loadRecipients() {
        Timber.i("Loading recipients started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMapMaybe { student ->
                semesterRepository.getCurrentSemester(student)
                    .flatMapMaybe { reportingUnitRepository.getReportingUnit(student, it.unitId) }
                    .doOnSuccess { reportingUnit = it }
                    .flatMap { recipientRepository.getRecipients(student, 2, it).toMaybe() }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    showProgress(true)
                    showContent(false)
                }
            }
            .doFinally {
                view?.run {
                    showProgress(false)
                }
            }
            .subscribe({
                view?.apply {
                    setReportingUnit(reportingUnit)
                    setRecipients(it)
                    refreshRecipientsAdapter()
                    showContent(true)
                }
                Timber.i("Loading recipients result: Success, fetched %s recipients", it.size.toString())
            }, {
                Timber.i("Loading recipients result: An exception occurred")
                view?.showContent(true)
                errorHandler.dispatch(it)
            }, {
                Timber.i("Loading recipients result: Can't find the reporting unit")
                view?.showEmpty(true)
            })
        )
    }

    private fun sendMessage(subject: String, content: String, recipients: List<Recipient>) {
        Timber.i("Sending message started")
        disposable.add(messageRepository.sendMessage(subject, content, recipients)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    hideSoftInput()
                    showContent(false)
                    showProgress(true)
                }
            }
            .doFinally {
                view?.showProgress(false)
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
                view?.showContent(true)
                errorHandler.dispatch(it)
            })
        )
    }

    fun onTypingRecipients() {
        view?.refreshRecipientsAdapter()
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

    override fun onDetachView() {
        view?.showBottomNav(true)
        super.onDetachView()
    }
}
