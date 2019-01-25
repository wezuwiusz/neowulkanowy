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
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.ui.modules.main.MainActivity

class LuckyNumberNotification(context: Context) : BaseNotification(context) {

    private val channelId = "Lucky_Number_Notify"

    @TargetApi(26)
    override fun createChannel(channelId: String) {
        notificationManager.createNotificationChannel(NotificationChannel(
            channelId, context.getString(R.string.notify_lucky_number_channel), NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        })
    }

    fun sendNotification(luckyNumber: LuckyNumber) {
        notify(notificationBuilder(channelId)
            .setContentTitle(context.getString(R.string.notify_lucky_number_new_item_title))
            .setContentText(context.getString(R.string.notify_lucky_number_new_item, luckyNumber.luckyNumber))
            .setSmallIcon(R.drawable.ic_stat_notify_lucky_number)
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
            .build()
        )
    }
}
