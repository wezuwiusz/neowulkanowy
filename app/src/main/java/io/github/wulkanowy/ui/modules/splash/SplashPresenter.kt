package io.github.wulkanowy.ui.modules.splash

import android.os.Build
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val appInfo: AppInfo
) : BasePresenter<SplashView>(errorHandler, studentRepository) {

    private var externalUrl: String? = null

    fun onAttachView(view: SplashView, externalUrl: String?) {
        super.onAttachView(view)
        this.externalUrl = externalUrl

        if (appInfo.systemVersion < Build.VERSION_CODES.LOLLIPOP && !preferencesRepository.isKitkatDialogDisabled) {
            view.showKitkatView()
        } else {
            loadCorrectDataOrUser()
        }
    }

    private fun loadCorrectDataOrUser() {
        if (!externalUrl.isNullOrBlank()) {
            view?.openExternalUrlAndFinish(externalUrl!!)
            return
        }

        flowWithResource { studentRepository.isCurrentStudentSet() }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Is current user set check started")
                Status.SUCCESS -> {
                    if (it.data!!) view?.openMainView()
                    else view?.openLoginView()
                }
                Status.ERROR -> errorHandler.dispatch(it.error!!)
            }
        }.launch()
    }

    fun onKitkatViewDismissed() {
        loadCorrectDataOrUser()
    }

    fun onNeutralButtonSelected() {
        preferencesRepository.isKitkatDialogDisabled = true
    }
}
