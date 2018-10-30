package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val preferencesRepository: PreferencesRepository,
    errorHandler: ErrorHandler
) : BasePresenter<SplashView>(errorHandler) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        view.run {
            setCurrentThemeMode(preferencesRepository.currentTheme)
            if (sessionRepository.isSessionSaved) openMainView() else openLoginView()
        }
    }
}
