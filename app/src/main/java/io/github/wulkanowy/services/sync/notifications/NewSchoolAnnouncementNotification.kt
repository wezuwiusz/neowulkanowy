package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewSchoolAnnouncementNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<SchoolAnnouncement>, student: Student) {
        val notification = MultipleNotifications(
            type = NotificationType.NEW_ANNOUNCEMENT,
            icon = R.drawable.ic_all_about,
            titleStringRes = R.plurals.school_announcement_notify_new_item_title,
            contentStringRes = R.plurals.school_announcement_notify_new_items,
            summaryStringRes = R.plurals.school_announcement_number_item,
            startMenu = MainView.Section.SCHOOL_ANNOUNCEMENT,
            lines = items.map {
                "${it.subject}: ${it.content}"
            }
        )

        sendNotification(notification, student)
    }
}
