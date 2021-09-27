package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import javax.inject.Inject

class NewHomeworkNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

    suspend fun notify(items: List<Homework>, student: Student) {
        val today = LocalDate.now()
        val lines = items.filter { !it.date.isBefore(today) }.map {
            "${it.date.toFormattedString("dd.MM")} - ${it.subject}: ${it.content}"
        }.ifEmpty { return }

        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_HOMEWORK,
            icon = R.drawable.ic_more_homework,
            titleStringRes = R.plurals.homework_notify_new_item_title,
            contentStringRes = R.plurals.homework_notify_new_item_content,
            summaryStringRes = R.plurals.homework_number_item,
            startMenu = MainView.Section.HOMEWORK,
            lines = lines
        )

        appNotificationManager.sendNotification(notification, student)
    }
}
