package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.ui.base.BaseView

interface MenuOrderView : BaseView {

    fun initView()

    fun updateData(data: List<MenuOrderItem>)

    fun restartApp()

    fun showRestartConfirmationDialog()

    fun popView()
}
