package io.github.wulkanowy.ui.modules.message.preview

import com.google.firebase.analytics.FirebaseAnalytics.Param.START_DATE
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messageRepository: MessageRepository,
    private val studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<MessagePreviewView>(errorHandler) {

    var messageId: Int = 0

    fun onAttachView(view: MessagePreviewView, id: Int) {
        super.onAttachView(view)
        loadData(id)
    }

    private fun loadData(id: Int) {
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
                    view?.run {
                        message.let {
                            setSubject(if (it.subject.isNotBlank()) it.subject else noSubjectString)
                            setDate(it.date?.toFormattedString("yyyy-MM-dd HH:mm:ss"))
                            setContent(it.content)

                            if (it.recipient?.isNotBlank() == true) setRecipient(it.recipient)
                            else setSender(it.sender)
                        }
                    }
                    analytics.logEvent("load_message_preview", START_DATE to message.date?.toFormattedString("yyyy.MM.dd"), "lenght" to message.content?.length)
                }) {
                    Timber.i("Loading message $id preview result: An exception occurred ")
                    view?.showMessageError()
                    errorHandler.dispatch(it)
                })
        }
    }
}
