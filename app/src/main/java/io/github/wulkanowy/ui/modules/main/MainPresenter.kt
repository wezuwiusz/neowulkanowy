package io.github.wulkanowy.ui.modules.main

import com.google.firebase.analytics.FirebaseAnalytics.Event.APP_OPEN
import com.google.firebase.analytics.FirebaseAnalytics.Param.DESTINATION
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val studentRepository: StudentRepository,
    private val prefRepository: PreferencesRepository,
    private val schedulers: SchedulersProvider,
    private val serviceHelper: ServiceHelper,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MainView>(errorHandler) {

    fun onAttachView(view: MainView, initMenuIndex: Int) {
        super.onAttachView(view)
        view.run {
            cancelNotifications()
            startMenuIndex = if (initMenuIndex != -1) initMenuIndex else prefRepository.startMenuIndex
            initView()
        }
        serviceHelper.startFullSyncService()

        analytics.logEvent(APP_OPEN, mapOf(DESTINATION to when (initMenuIndex) {
            1 -> "Grades"
            3 -> "Timetable"
            4 -> "More"
            else -> "User action"
        }))
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

    fun onAccountManagerSelected(): Boolean {
        view?.showAccountPicker()
        return true
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

    fun onLoginSelected() {
        disposable.add(studentRepository.getCurrentStudent(false)
            .flatMapCompletable { studentRepository.logoutStudent(it) }
            .andThen(studentRepository.getSavedStudents(false))
            .flatMapCompletable {
                if (it.isNotEmpty()) studentRepository.switchStudent(it[0])
                else Completable.complete()
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.openLoginView() }, { errorHandler.dispatch(it) }))
    }
}
