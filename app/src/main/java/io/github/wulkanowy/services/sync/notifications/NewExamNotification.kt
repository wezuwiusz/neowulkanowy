package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewExamNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<Exam>, student: Student) {
        val notification = MultipleNotifications(
            type = NotificationType.NEW_EXAM,
            icon = R.drawable.ic_main_exam,
            titleStringRes = R.plurals.exam_notify_new_item_title,
            contentStringRes = R.plurals.exam_notify_new_item_title, // TODO add missing string
            summaryStringRes = R.plurals.exam_number_item,
            startMenu = MainView.Section.EXAM,
            lines = items.map {
                "${it.subject}: ${it.description}"
            }
        )

        sendNotification(notification, student)
    }
}
