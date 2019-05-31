package io.github.wulkanowy.ui.modules.main

import com.google.firebase.analytics.FirebaseAnalytics.Event.APP_OPEN
import com.google.firebase.analytics.FirebaseAnalytics.Param.DESTINATION
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val prefRepository: PreferencesRepository,
    private val syncManager: SyncManager,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MainView>(errorHandler, studentRepository, schedulers) {

    fun onAttachView(view: MainView, initMenu: MainView.MenuView?) {
        super.onAttachView(view)
        view.apply {
            getProperViewIndexes(initMenu).let { (main, more) ->
                startMenuIndex = main
                startMenuMoreIndex = more
            }
            initView()
            Timber.i("Main view was initialized with $startMenuIndex menu index and $startMenuMoreIndex more index")
        }

        syncManager.startSyncWorker()
        analytics.logEvent(APP_OPEN, DESTINATION to initMenu?.name)
    }

    fun onViewChange() {
        view?.apply {
            currentViewTitle?.let { setViewTitle(it) }
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
                switchMenuView(index)
                true
            }
        } == true
    }

    private fun getProperViewIndexes(initMenu: MainView.MenuView?): Pair<Int, Int> {
        return when {
            initMenu?.id in 0..3 -> initMenu!!.id to -1
            (initMenu?.id ?: 0) > 3 -> 4 to initMenu!!.id - 4
            else -> prefRepository.startMenuIndex to -1
        }
    }
}
