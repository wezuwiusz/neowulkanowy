package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.Destination

interface SplashView : BaseView {

    fun openLoginView()

    fun openMainView(destination: Destination?)

    fun openExternalUrlAndFinish(url: String)
}
