package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewMessageNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

    suspend fun notify(items: List<Message>, student: Student) {
        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_MESSAGE,
            icon = R.drawable.ic_stat_message,
            titleStringRes = R.plurals.message_new_items,
            contentStringRes = R.plurals.message_notify_new_items,
            summaryStringRes = R.plurals.message_number_item,
            startMenu = MainView.Section.MESSAGE,
            lines = items.map {
                "${it.sender}: ${it.subject}"
            }
        )

        appNotificationManager.sendNotification(notification, student)
    }
}
