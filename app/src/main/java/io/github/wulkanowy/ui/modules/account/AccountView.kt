package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.ui.base.BaseView

interface AccountView : BaseView {

    fun initView()

    fun updateData(data: List<AccountItem>)

    fun dismissView()

    fun showConfirmDialog()

    fun openLoginView()

    fun recreateView()
}

