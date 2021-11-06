package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getPlural
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDateTime
import javax.inject.Inject

class NewConferenceNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Conference>, student: Student) {
        val today = LocalDateTime.now()
        val lines = items.filter { !it.date.isBefore(today) }
            .map {
                "${it.date.toFormattedString("dd.MM")} - ${it.title}: ${it.subject}"
            }
            .ifEmpty { return }

        val notificationDataList = lines.map {
            NotificationData(
                title = context.getPlural(R.plurals.conference_notify_new_item_title, 1),
                content = it,
                intentToStart = MainActivity.getStartIntent(context, Destination.Conference, true)
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(R.plurals.conference_notify_new_item_title, lines.size),
            content = context.getPlural(
                R.plurals.conference_notify_new_items,
                lines.size,
                lines.size
            ),
            intentToStart = MainActivity.getStartIntent(context, Destination.Conference, true),
            type = NotificationType.NEW_CONFERENCE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
