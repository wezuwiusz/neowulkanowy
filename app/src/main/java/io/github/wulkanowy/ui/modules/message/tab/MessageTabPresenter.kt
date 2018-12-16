package io.github.wulkanowy.ui.modules.message.tab

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MessageTabPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository,
    private val studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<MessageTabView>(errorHandler) {

    lateinit var folder: MessagesRepository.MessageFolder

    fun onAttachView(view: MessageTabView, folder: MessagesRepository.MessageFolder) {
        super.onAttachView(view)
        view.initView()
        this.folder = folder
    }

    fun onSwipeRefresh() {
        onParentViewLoadData(true)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { messagesRepository.getMessages(it.studentId, folder, forceRefresh) }
                .map { items -> items.map { MessageItem(it, view?.noSubjectString.orEmpty()) } }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        notifyParentDataLoaded()
                    }
                }
                .subscribe({
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        updateData(it)
                    }
                    analytics.logEvent("load_messages", mapOf("items" to it.size, "folder" to folder.name))
                }) {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    fun onMessageItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is MessageItem) {
            view?.run {
                openMessage(item.message.realId)
                if (item.message.unread == true) {
                    item.message.unread = false
                    updateItem(item)
                    updateMessage(item.message)
                }
            }
        }
    }

    private fun updateMessage(message: Message) {
        disposable.add(messagesRepository.updateMessage(message)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.d("Message ${message.realId} updated")
            }) { error -> errorHandler.dispatch(error) }
        )
    }
}
