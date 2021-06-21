package io.github.wulkanowy.ui.modules.debug

import io.github.wulkanowy.ui.base.BaseView

interface DebugView : BaseView {

    fun initView()

    fun setItems(itemList: List<DebugItem>)

    fun openLogViewer()

    fun openNotificationsDebug()
}
