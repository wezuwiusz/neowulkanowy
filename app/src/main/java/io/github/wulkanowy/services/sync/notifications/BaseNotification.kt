package io.github.wulkanowy.services.sync.notifications

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.PluralsRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.data.pojos.Notification
import io.github.wulkanowy.data.pojos.OneNotification
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.nickOrName
import kotlin.random.Random

abstract class BaseNotification(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {

    protected fun sendNotification(notification: Notification, student: Student) =
        when (notification) {
            is OneNotification -> sendOneNotification(notification, student)
            is MultipleNotifications -> sendMultipleNotifications(notification, student)
        }

    private fun sendOneNotification(notification: OneNotification, student: Student?) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            getNotificationBuilder(notification).apply {
                val content = context.getString(
                    notification.contentStringRes,
                    *notification.contentValues.toTypedArray()
                )
                setContentTitle(context.getString(notification.titleStringRes))
                setContentText(content)
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .setSummaryText(student?.nickOrName)
                        .bigText(content)
                )
            }.build()
        )
    }

    private fun sendMultipleNotifications(notification: MultipleNotifications, student: Student) {
        val group = notification.type.group + student.id
        val groupId = student.id * 100 + notification.type.ordinal

        notification.lines.forEach { item ->
            notificationManager.notify(
                Random.nextInt(Int.MAX_VALUE),
                getNotificationBuilder(notification).apply {
                    setContentTitle(getQuantityString(notification.titleStringRes, 1))
                    setContentText(item)
                    setStyle(
                        NotificationCompat.BigTextStyle()
                            .setSummaryText(student.nickOrName)
                            .bigText(item)
                    )
                    setGroup(group)
                }.build()
            )
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        notificationManager.notify(
            groupId.toInt(),
            getNotificationBuilder(notification).apply {
                setSmallIcon(notification.icon)
                setGroup(group)
                setStyle(NotificationCompat.InboxStyle().setSummaryText(student.nickOrName))
                setGroupSummary(true)
            }.build()
        )
    }

    private fun getNotificationBuilder(notification: Notification) = NotificationCompat
        .Builder(context, notification.type.channel)
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
