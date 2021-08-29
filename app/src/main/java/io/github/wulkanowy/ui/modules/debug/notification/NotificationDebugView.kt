package io.github.wulkanowy.ui.modules.debug.notification

import io.github.wulkanowy.ui.base.BaseView

interface NotificationDebugView : BaseView {

    fun initView()

    fun setItems(notificationDebugs: List<NotificationDebugItem>)
}
