package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.services.sync.channels.NewGradesChannel
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewGradeNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

     fun notifyDetails(items: List<Grade>) {
        val notification = MultipleNotifications(
            channelId = NewGradesChannel.CHANNEL_ID,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items,
            contentStringRes = R.plurals.grade_notify_new_items,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.entry}"
            }
        )

        sendNotification(notification)
    }

     fun notifyPredicted(items: List<GradeSummary>) {
        val notification = MultipleNotifications(
            channelId = NewGradesChannel.CHANNEL_ID,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items_predicted,
            contentStringRes = R.plurals.grade_notify_new_items_predicted,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.predictedGrade}"
            }
        )

        sendNotification(notification)
    }

     fun notifyFinal(items: List<GradeSummary>) {
        val notification = MultipleNotifications(
            channelId = NewGradesChannel.CHANNEL_ID,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items_final,
            contentStringRes = R.plurals.grade_notify_new_items_final,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.finalGrade}"
            }
        )

        sendNotification(notification)
    }
}
