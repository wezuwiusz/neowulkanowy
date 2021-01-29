package io.github.wulkanowy.ui.modules.account.accountquick

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.account.AccountItem

interface AccountQuickView : BaseView {

    fun initView()

    fun updateData(data: List<AccountItem<*>>)

    fun recreateMainView()

    fun popView()

    fun openAccountView()
}
