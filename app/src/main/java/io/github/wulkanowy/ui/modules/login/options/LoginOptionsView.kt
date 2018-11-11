package io.github.wulkanowy.ui.modules.login.options

import io.github.wulkanowy.ui.base.BaseView

interface LoginOptionsView : BaseView {

    fun initView()

    fun updateData(data: List<LoginOptionsItem>)

    fun openMainView()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showActionBar(show: Boolean)
}
