package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val prefRepository: PreferencesRepository,
    private val serviceHelper: ServiceHelper
) : BasePresenter<MainView>(errorHandler) {

    fun onAttachView(view: MainView, init: Int) {
        super.onAttachView(view)

        view.run {
            cancelNotifications()
            startMenuIndex = if (init != -1) init else prefRepository.startMenuIndex
            initView()
        }

        serviceHelper.startFullSyncService()
    }

    fun onViewStart() {
        view?.apply {
            currentViewTitle?.let { setViewTitle(it) }
            currentStackSize?.let {
                if (it > 1) showHomeArrow(true)
                else showHomeArrow(false)
            }
        }
    }

    fun onUpNavigate(): Boolean {
        view?.popView()
        return true
    }

    fun onBackPressed(default: () -> Unit) {
        view?.run {
            if (isRootView) default()
            else popView()
        }
    }

    fun onTabSelected(index: Int, wasSelected: Boolean): Boolean {
        return view?.run {
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
