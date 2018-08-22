package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.ErrorHandler
import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<T : BaseView>(private val errorHandler: ErrorHandler) {

    val disposable = CompositeDisposable()

    var view: T? = null

    val isViewAttached: Boolean
        get() = view != null

    open fun attachView(view: T) {
        this.view = view
        errorHandler.showErrorMessage = { view.showMessage(it) }
    }

    open fun detachView() {
        view = null
        disposable.clear()
        errorHandler.clear()
    }
}
