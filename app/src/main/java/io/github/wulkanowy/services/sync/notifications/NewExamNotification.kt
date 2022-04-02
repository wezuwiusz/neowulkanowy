package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.getPlural
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import javax.inject.Inject

class NewExamNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Exam>, student: Student) {
        val today = LocalDate.now()
        val lines = items.filter { !it.date.isBefore(today) }
            .map {
                "${it.date.toFormattedString("dd.MM")} - ${it.subject}: ${it.description}"
            }
            .ifEmpty { return }

        val notificationDataList = lines.map {
            NotificationData(
                title = context.getPlural(R.plurals.exam_notify_new_item_title, 1),
                content = it,
                destination = Destination.Exam,
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(R.plurals.exam_notify_new_item_title, lines.size),
            content = context.getPlural(
                R.plurals.exam_notify_new_item_content,
                lines.size,
                lines.size
            ),
            destination = Destination.Exam,
            type = NotificationType.NEW_EXAM
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
