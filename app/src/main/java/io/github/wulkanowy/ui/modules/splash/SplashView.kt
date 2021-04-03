package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.ui.base.BaseView

interface SplashView : BaseView {

    fun openLoginView()

    fun openMainView()

    fun openExternalUrlAndFinish(url: String)
}
