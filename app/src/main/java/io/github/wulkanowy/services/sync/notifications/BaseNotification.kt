package io.github.wulkanowy.services.sync.notifications

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.PluralsRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.data.pojos.Notification
import io.github.wulkanowy.data.pojos.OneNotification
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import kotlin.random.Random

abstract class BaseNotification(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {

    protected fun sendNotification(notification: Notification) = when (notification) {
        is OneNotification -> sendOneNotification(notification)
        is MultipleNotifications -> sendMultipleNotifications(notification)
    }

    private fun sendOneNotification(notification: OneNotification) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            getNotificationBuilder(notification).apply {
                val content = context.getString(
                    notification.contentStringRes,
                    *notification.contentValues.toTypedArray()
                )
                setContentTitle(context.getString(notification.titleStringRes))
                setContentText(content)
                setStyle(NotificationCompat.BigTextStyle().bigText(content))
            }.build()
        )
    }

    private fun sendMultipleNotifications(notification: MultipleNotifications) {
        notification.lines.forEach { item ->
            notificationManager.notify(
                Random.nextInt(Int.MAX_VALUE),
                getNotificationBuilder(notification).apply {
                    setContentTitle(getQuantityString(notification.titleStringRes, 1))
                    setContentText(item)
                    setStyle(NotificationCompat.BigTextStyle().bigText(item))
                    setGroup(notification.group)
                }.build()
            )
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        notificationManager.notify(
            notification.group.hashCode(),
            getNotificationBuilder(notification).apply {
                setSmallIcon(notification.icon)
                setGroup(notification.group)
                setGroupSummary(true)
            }.build()
        )
    }

    private fun getNotificationBuilder(notification: Notification) = NotificationCompat
        .Builder(context, notification.channelId)
        .setLargeIcon(context.getCompatBitmap(notification.icon, R.color.colorPrimary))
        .setSmallIcon(R.drawable.ic_stat_all)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setColor(context.getCompatColor(R.color.colorPrimary))
        .setContentIntent(
            PendingIntent.getActivity(
                context, notification.startMenu.id,
                MainActivity.getStartIntent(context, notification.startMenu, true),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

    private fun getQuantityString(@PluralsRes id: Int, value: Int): String {
        return context.resources.getQuantityString(id, value, value)
    }
}
