package io.github.wulkanowy.ui.modules.message.tab

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
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
    private val messageRepository: MessageRepository,
    private val studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<MessageTabView>(errorHandler) {

    lateinit var folder: MessageRepository.MessageFolder

    fun onAttachView(view: MessageTabView, folder: MessageRepository.MessageFolder) {
        super.onAttachView(view)
        view.initView()
        this.folder = folder
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the $folder message")
        onParentViewLoadData(true)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        Timber.i("Loading $folder message data started")
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { messageRepository.getMessages(it, folder, forceRefresh) }
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
                    Timber.i("Loading $folder message result: Success")
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        updateData(it)
                    }
                    analytics.logEvent("load_messages", "items" to it.size, "folder" to folder.name)
                }) {
                    Timber.i("Loading $folder message result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    fun onMessageItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is MessageItem) {
            Timber.i("Select message ${item.message.realId} item")
            view?.run {
                openMessage(item.message.realId)
                if (item.message.unread) {
                    item.message.unread = false
                    updateItem(item)
                    updateMessage(item.message)
                }
            }
        }
    }

    private fun updateMessage(message: Message) {
        Timber.i("Attempt to update message ${message.realId}")
        disposable.add(messageRepository.updateMessage(message)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ Timber.d("Update message ${message.realId} result: Success") })
            { error ->
                Timber.i("Update message ${message.realId} result: An exception occurred")
                errorHandler.dispatch(error)
            })
    }
}
