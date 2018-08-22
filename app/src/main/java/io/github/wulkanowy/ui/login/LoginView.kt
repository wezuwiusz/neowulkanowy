package io.github.wulkanowy.ui.login

import io.github.wulkanowy.ui.base.BaseView

interface LoginView : BaseView {

    fun initAdapter()

    fun loadOptionsView(index: Int)

    fun switchView(position: Int)

    fun hideActionBar()

    fun currentViewPosition(): Int
}
