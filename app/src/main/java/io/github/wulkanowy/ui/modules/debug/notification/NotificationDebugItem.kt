package io.github.wulkanowy.ui.modules.debug.notification

import androidx.annotation.StringRes

data class NotificationDebugItem(
    @StringRes val title: Int,
    val onClickCallback: (numberOfNotifications: Int) -> Unit,
)
