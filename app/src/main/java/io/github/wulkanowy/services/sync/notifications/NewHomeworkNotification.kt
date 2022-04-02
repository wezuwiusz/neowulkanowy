package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.getPlural
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import javax.inject.Inject

class NewHomeworkNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Homework>, student: Student) {
        val today = LocalDate.now()
        val lines = items.filter { !it.date.isBefore(today) }
            .map {
                "${it.date.toFormattedString("dd.MM")} - ${it.subject}: ${it.content}"
            }
            .ifEmpty { return }

        val notificationDataList = lines.map {
            NotificationData(
                title = context.getPlural(R.plurals.homework_notify_new_item_title, 1),
                content = it,
                destination = Destination.Homework,
            )
        }

        val groupNotificationData = GroupNotificationData(
            title = context.getPlural(R.plurals.homework_notify_new_item_title, lines.size),
            content = context.getPlural(
                R.plurals.homework_notify_new_item_content,
                lines.size,
                lines.size
            ),
            destination = Destination.Homework,
            type = NotificationType.NEW_HOMEWORK,
            notificationDataList = notificationDataList
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
