package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.services.sync.channels.NewExamChannel
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewExamNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<Exam>) {
        val notification = MultipleNotifications(
            channelId = NewExamChannel.CHANNEL_ID,
            icon = R.drawable.ic_main_exam,
            titleStringRes = R.plurals.exam_notify_new_item_title,
            contentStringRes = R.plurals.grade_notify_new_items, // TODO add missing string
            summaryStringRes = R.plurals.exam_number_item,
            startMenu = MainView.Section.EXAM,
            lines = items.map {
                "${it.subject}: ${it.description}"
            }
        )

        sendNotification(notification)
    }
}
