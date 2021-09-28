package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.OneNotificationData
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewLuckyNumberNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

   suspend fun notify(item: LuckyNumber, student: Student) {
       val notification = OneNotificationData(
           type = NotificationType.NEW_LUCKY_NUMBER,
           icon = R.drawable.ic_stat_luckynumber,
           titleStringRes = R.string.lucky_number_notify_new_item_title,
           contentStringRes = R.string.lucky_number_notify_new_item,
           startMenu = MainView.Section.LUCKY_NUMBER,
           contentValues = listOf(item.luckyNumber.toString())
       )

       appNotificationManager.sendNotification(notification, student)
    }
}
