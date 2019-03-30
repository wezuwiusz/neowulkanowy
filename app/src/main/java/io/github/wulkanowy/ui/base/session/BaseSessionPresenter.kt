package io.github.wulkanowy.ui.base.session

import io.github.wulkanowy.ui.base.BasePresenter

open class BaseSessionPresenter<T : BaseSessionView>(private val errorHandler: SessionErrorHandler) :
    BasePresenter<T>(errorHandler) {

    override fun onAttachView(view: T) {
        super.onAttachView(view)
        errorHandler.apply {
            onDecryptionFail = { view.showExpiredDialog() }
            onNoCurrentStudent = { view.openLoginView() }
        }
    }
}
