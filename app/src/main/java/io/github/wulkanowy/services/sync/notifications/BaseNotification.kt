package io.github.wulkanowy.services.sync.notifications

import android.app.PendingIntent
import android.content.Context
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

    protected fun sendNotification(notification: Notification) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, notification.channelId)
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
                .apply {
                    when (notification) {
                        is OneNotification -> buildForOneNotification(notification)
                        is MultipleNotifications -> buildForMultipleNotification(notification)
                    }
                }
                .build()
        )
    }

    private fun NotificationCompat.Builder.buildForOneNotification(n: OneNotification) {
        val content = context.getString(n.contentStringRes, *n.contentValues.toTypedArray())
        setContentTitle(context.getString(n.titleStringRes))
        setContentText(content)
        setStyle(NotificationCompat.BigTextStyle().run {
            bigText(content)
            this
        })
    }

    private fun NotificationCompat.Builder.buildForMultipleNotification(n: MultipleNotifications) {
        val lines = n.lines.size
        setContentTitle(context.resources.getQuantityString(n.titleStringRes, lines, lines))
        setContentText(context.resources.getQuantityString(n.contentStringRes, lines, lines))
        setStyle(NotificationCompat.InboxStyle().run {
            setSummaryText(
                context.resources.getQuantityString(n.summaryStringRes, n.lines.size, n.lines.size)
            )
            n.lines.forEach(::addLine)
            this
        })
    }
}
