package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor(errorHandler: ErrorHandler) : BasePresenter<LoginView>(errorHandler) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        view.run {
            initAdapter()
            hideActionBar()
        }
        Timber.i("Login view is attached")
    }

    fun onPageSelected(index: Int) {
        if (index == 1) view?.notifyOptionsViewLoadData()
    }

    fun onChildViewSwitchOptions() {
        view?.switchView(1)
    }

    fun onBackPressed(default: () -> Unit) {
        Timber.i("Back pressed in login view")
        view?.run {
            if (currentViewIndex == 1) {
                switchView(0)
                hideActionBar()
            } else default()
        }
    }
}
