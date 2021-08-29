package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDateTime
import javax.inject.Inject

class NewConferenceNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<Conference>, student: Student) {
        val today = LocalDateTime.now()
        val lines = items.filter { !it.date.isBefore(today) }.map {
            "${it.date.toFormattedString("dd.MM")} - ${it.title}: ${it.subject}"
        }.ifEmpty { return }

        val notification = MultipleNotifications(
            type = NotificationType.NEW_CONFERENCE,
            icon = R.drawable.ic_more_conferences,
            titleStringRes = R.plurals.conference_notify_new_item_title,
            contentStringRes = R.plurals.conference_notify_new_items,
            summaryStringRes = R.plurals.conference_number_item,
            startMenu = MainView.Section.CONFERENCE,
            lines = lines
        )

        sendNotification(notification, student)
    }
}
