package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.ui.base.BaseView

interface LoginView : BaseView {

    val currentViewIndex: Int

    fun initAdapter()

    fun hideActionBar()

    fun switchView(index: Int)

    fun notifyOptionsViewLoadData()
}
