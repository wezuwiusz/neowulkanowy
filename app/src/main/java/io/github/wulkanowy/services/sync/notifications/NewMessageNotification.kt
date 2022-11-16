package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.utils.getPlural
import javax.inject.Inject

class NewMessageNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Message>, student: Student) {
        val notificationDataList = items.map {
            NotificationData(
                title = context.getPlural(R.plurals.message_new_items, 1),
                content = "${it.correspondents}: ${it.subject}",
                destination = Destination.Message,
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(R.plurals.message_new_items, items.size),
            content = context.getPlural(R.plurals.message_notify_new_items, items.size, items.size),
            destination = Destination.Message,
            type = NotificationType.NEW_MESSAGE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
