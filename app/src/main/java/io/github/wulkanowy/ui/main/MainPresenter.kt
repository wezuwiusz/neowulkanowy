package io.github.wulkanowy.ui.main

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(errorHandler: ErrorHandler)
    : BasePresenter<MainView>(errorHandler) {

    override fun attachView(view: MainView) {
        super.attachView(view)
        view.run {
            initFragmentController()
            initBottomNav()
        }
    }

    fun onTabSelected(position: Int): Boolean {
        view?.switchMenuFragment(position)
        return true
    }

    fun onMenuFragmentChange(position: Int) {
        view?.run {
            setViewTitle(mapOfTitles()[position] ?: defaultTitle())
        }
    }
}

