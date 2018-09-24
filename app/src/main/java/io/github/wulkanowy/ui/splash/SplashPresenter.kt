package io.github.wulkanowy.ui.splash

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SplashPresenter @Inject constructor(private val sessionRepository: SessionRepository,
                                          errorHandler: ErrorHandler)
    : BasePresenter<SplashView>(errorHandler) {

    override fun attachView(view: SplashView) {
        super.attachView(view)
        view.run { if (sessionRepository.isSessionSaved) openMainView() else openLoginView() }
    }
}
