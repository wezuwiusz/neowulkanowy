package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewSchoolAnnouncementNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

   suspend fun notify(items: List<SchoolAnnouncement>, student: Student) {
       val notification = MultipleNotificationsData(
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

        appNotificationManager.sendNotification(notification, student)
    }
}
