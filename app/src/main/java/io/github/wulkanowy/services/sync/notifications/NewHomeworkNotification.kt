package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewHomeworkNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<Homework>, student: Student) {
        val notification = MultipleNotifications(
            type = NotificationType.NEW_HOMEWORK,
            icon = R.drawable.ic_more_homework,
            titleStringRes = R.plurals.homework_notify_new_item_title,
            contentStringRes = R.plurals.homework_notify_new_item_title, // todo: you received %d new homework
            summaryStringRes = R.plurals.homework_number_item,
            startMenu = MainView.Section.HOMEWORK,
            lines = items.map {
                "${it.subject}: ${it.content}"
            }
        )

        sendNotification(notification, student)
    }
}
