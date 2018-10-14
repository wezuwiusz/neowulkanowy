package io.github.wulkanowy.ui.login

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class LoginPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<LoginView>(errorHandler) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        view.run {
            initAdapter()
            hideActionBar()
        }
    }

    fun onPageSelected(index: Int) {
        if (index == 1) view?.loadOptionsView(index)
    }

    fun onSwitchFragment(position: Int) {
        view?.switchView(position)
    }

    fun onBackPressed(default: () -> Unit) {
        view?.run {
            if (currentViewPosition() == 1) {
                switchView(0)
                hideActionBar()
            } else default()
        }
    }
}
