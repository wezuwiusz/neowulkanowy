package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.main.MainView.Section.GRADE
import io.github.wulkanowy.ui.modules.main.MainView.Section.MESSAGE
import io.github.wulkanowy.ui.modules.main.MainView.Section.SCHOOL
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val prefRepository: PreferencesRepository,
    private val syncManager: SyncManager,
    private val analytics: AnalyticsHelper
) : BasePresenter<MainView>(errorHandler, studentRepository) {

    fun onAttachView(view: MainView, initMenu: MainView.Section?) {
        super.onAttachView(view)
        view.apply {
            getProperViewIndexes(initMenu).let { (main, more) ->
                startMenuIndex = main
                startMenuMoreIndex = more
            }
            initView()
            Timber.i("Main view was initialized with $startMenuIndex menu index and $startMenuMoreIndex more index")
        }

        syncManager.startPeriodicSyncWorker()
        analytics.logEvent("app_open", "destination" to initMenu?.name)
    }

    fun onViewChange(section: MainView.Section?) {
        view?.apply {
            showActionBarElevation(section != GRADE && section != MESSAGE && section != SCHOOL)
            currentViewTitle?.let { setViewTitle(it) }
            currentViewSubtitle?.let { setViewSubTitle(it.ifBlank { null }) }
            currentStackSize?.let {
                if (it > 1) showHomeArrow(true)
                else showHomeArrow(false)
            }
        }
    }

    fun onAccountManagerSelected(): Boolean {
        Timber.i("Select account manager")
        view?.showAccountPicker()
        return true
    }

    fun onUpNavigate(): Boolean {
        Timber.i("Up navigate pressed")
        view?.popView()
        return true
    }

    fun onBackPressed(default: () -> Unit) {
        Timber.i("Back pressed in main view")
        view?.run {
            if (isRootView) default()
            else popView()
        }
    }

    fun onTabSelected(index: Int, wasSelected: Boolean): Boolean {
        return view?.run {
            Timber.i("Switch main tab index: $index, reselected: $wasSelected")
            if (wasSelected) {
                notifyMenuViewReselected()
                false
            } else {
                notifyMenuViewChanged()
                switchMenuView(index)
                true
            }
        } == true
    }

    private fun getProperViewIndexes(initMenu: MainView.Section?): Pair<Int, Int> {
        return when (initMenu?.id) {
            in 0..3 -> initMenu!!.id to -1
            in 4..10 -> 4 to initMenu!!.id
            else -> prefRepository.startMenuIndex to -1
        }
    }
}
