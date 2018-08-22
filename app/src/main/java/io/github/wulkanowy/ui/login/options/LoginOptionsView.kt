package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.ui.base.BaseView

interface LoginOptionsView : BaseView {

    fun updateData(data: List<LoginOptionsItem>)

    fun initRecycler()

    fun openMainView()

    fun showLoginProgress(show: Boolean)

    fun showActionBar(show: Boolean)
}
