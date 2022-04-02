package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.services.sync.notifications.NotificationType
import io.github.wulkanowy.ui.modules.Destination

data class NotificationData(
    val destination: Destination,
    val title: String,
    val content: String
)

data class GroupNotificationData(
    val notificationDataList: List<NotificationData>,
    val title: String,
    val content: String,
    val destination: Destination,
    val type: NotificationType
)

