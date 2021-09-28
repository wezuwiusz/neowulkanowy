package io.github.wulkanowy.ui.modules.notificationscenter

import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.ui.base.BaseView

interface NotificationsCenterView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<Notification>)

    fun showProgress(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showContent(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)
}