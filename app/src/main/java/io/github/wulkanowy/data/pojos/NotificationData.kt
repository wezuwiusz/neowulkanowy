package io.github.wulkanowy.data.pojos

import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import io.github.wulkanowy.services.sync.notifications.NotificationType
import io.github.wulkanowy.ui.modules.main.MainView

sealed interface NotificationData {
    val type: NotificationType
    val startMenu: MainView.Section
    val icon: Int
    val titleStringRes: Int
    val contentStringRes: Int
}

data class MultipleNotificationsData(
    override val type: NotificationType,
    override val startMenu: MainView.Section,
    @DrawableRes override val icon: Int,
    @PluralsRes override val titleStringRes: Int,
    @PluralsRes override val contentStringRes: Int,

    @PluralsRes val summaryStringRes: Int,
    val lines: List<String>,
) : NotificationData

data class OneNotificationData(
    override val type: NotificationType,
    override val startMenu: MainView.Section,
    @DrawableRes override val icon: Int,
    @StringRes override val titleStringRes: Int,
    @StringRes override val contentStringRes: Int,

    val contentValues: List<String>,
) : NotificationData
