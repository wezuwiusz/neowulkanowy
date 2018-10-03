package io.github.wulkanowy.ui.main

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<MainView>(errorHandler) {

    override fun attachView(view: MainView) {
        super.attachView(view)
        view.initView()
    }

    fun onStartView() {
        view?.run { setViewTitle(viewTitle(currentMenuIndex())) }
    }

    fun onMenuViewChange(index: Int) {
        view?.run { setViewTitle(viewTitle(index)) }
    }

    fun onTabSelected(index: Int, wasSelected: Boolean): Boolean {
        return view?.run {
            expandActionBar(true)
            if (wasSelected) {
                notifyMenuViewReselected()
                false
            } else {
                switchMenuView(index)
                true
            }
        } == true
    }
}
