package io.github.wulkanowy.ui.modules.message

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class MessagePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<MessageView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: MessageView) {
        super.onAttachView(view)
        disposable.add(Completable.timer(150, MILLISECONDS, schedulers.mainThread)
            .subscribe {
                view.initView()
                Timber.i("Message view was initialized")
                loadData()
            })
    }

    fun onPageSelected(index: Int) {
        loadChild(index)
    }

    private fun loadData() {
        view?.run { loadChild(currentPageIndex) }
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
        Timber.i("Load message child view index: $index")
        view?.notifyChildLoadData(index, forceRefresh)
    }

    fun onChildViewLoaded() {
        view?.apply {
            showContent(true)
            showProgress(false)
        }
    }

    fun onDeleteMessage(message: Message) {
        view?.notifyChildMessageDeleted(
            when (message.removed) {
                true -> 2
                else -> message.folderId - 1
            }
        )
    }

    fun onSendMessageButtonClicked() {
        view?.openSendMessage()
    }
}
