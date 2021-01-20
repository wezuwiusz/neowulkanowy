package io.github.wulkanowy.ui.modules.message

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MessagePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<MessageView>(errorHandler, studentRepository) {

    override fun onAttachView(view: MessageView) {
        super.onAttachView(view)
        launch {
            delay(150)
            view.initView()
            Timber.i("Message view was initialized")
            loadData()
        }
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

    fun onSendMessageButtonClicked() {
        view?.openSendMessage()
    }
}
