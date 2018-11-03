package io.github.wulkanowy.services.notification

import android.annotation.TargetApi
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import timber.log.Timber

class GradeNotification(context: Context) : BaseNotification(context) {

    private val channelId = "Grade_Notify"

    @TargetApi(26)
    override fun createChannel(channelId: String) {
        notificationManager.createNotificationChannel(NotificationChannel(
            channelId, context.getString(R.string.notify_grade_channel), IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = VISIBILITY_PUBLIC
        })
    }

    fun sendNotification(items: List<Grade>) {
        notify(notificationBuilder(channelId)
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items, items.size, items.size))
            .setContentText(context.resources.getQuantityString(R.plurals.notify_grade_new_items, items.size, items.size))
            .setSmallIcon(R.drawable.ic_stat_notify_grade)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, 0,
                    MainActivity.getStartIntent(context).putExtra(EXTRA_START_MENU_INDEX, 0),
                    FLAG_UPDATE_CURRENT
                )
            )
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, items.size, items.size))
                items.forEach {
                    addLine("${it.subject}: ${it.entry}")
                }
                this
            })
            .build()
        )

        Timber.d("Notification sent")
    }
}
