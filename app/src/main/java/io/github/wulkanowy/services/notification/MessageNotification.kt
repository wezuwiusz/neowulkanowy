package io.github.wulkanowy.services.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.modules.main.MainActivity
import timber.log.Timber

class MessageNotification(context: Context) : BaseNotification(context) {

    private val channelId = "Message_Notify"

    @TargetApi(26)
    override fun createChannel(channelId: String) {
        notificationManager.createNotificationChannel(NotificationChannel(
            channelId, context.getString(R.string.notify_message_channel), NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        })
    }

    fun sendNotification(items: List<Message>) {
        notify(notificationBuilder(channelId)
            .setContentTitle(context.resources.getQuantityString(R.plurals.message_new_items, items.size, items.size))
            .setContentText(context.resources.getQuantityString(R.plurals.notify_message_new_items, items.size, items.size))
            .setSmallIcon(R.drawable.ic_stat_notify_message)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, 0,
                    MainActivity.getStartIntent(context).putExtra(MainActivity.EXTRA_START_MENU_INDEX, 4),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.message_number_item, items.size, items.size))
                items.forEach {
                    addLine("${it.sender}: ${it.subject}")
                }
                this
            })
            .build()
        )

        Timber.d("Notification sent")
    }
}
