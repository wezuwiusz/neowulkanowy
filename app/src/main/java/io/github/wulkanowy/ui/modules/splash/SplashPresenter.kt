package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.logLogin
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val sessionRepository: SessionRepository,
    errorHandler: ErrorHandler
) : BasePresenter<SplashView>(errorHandler) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        view.run {
            if (sessionRepository.isSessionSaved) {
                logLogin("Open app")
                openMainView()
            } else openLoginView()
        }
    }
}
