package io.github.wulkanowy.services.sync.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.PluralsRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.data.pojos.OneNotificationData
import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.nickOrName
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

class AppNotificationManager @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @ApplicationContext private val context: Context,
    private val appInfo: AppInfo,
    private val notificationRepository: NotificationRepository
) {

    suspend fun sendNotification(notificationData: NotificationData, student: Student) =
        when (notificationData) {
            is OneNotificationData -> sendOneNotification(notificationData, student)
            is MultipleNotificationsData -> sendMultipleNotifications(notificationData, student)
        }

    private suspend fun sendOneNotification(
        notificationData: OneNotificationData,
        student: Student
    ) {
        val content = context.getString(
            notificationData.contentStringRes,
            *notificationData.contentValues.toTypedArray()
        )

        val title = context.getString(notificationData.titleStringRes)

        val notification = getDefaultNotificationBuilder(notificationData)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(student.nickOrName)
                    .bigText(content)
            )
            .build()

        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), notification)

        saveNotification(title, content, notificationData, student)
    }

    private suspend fun sendMultipleNotifications(
        notificationData: MultipleNotificationsData,
        student: Student
    ) {
        val groupType = notificationData.type.group ?: return
        val group = "${groupType}_${student.id}"

        notificationData.sendSummaryNotification(group, student)

        notificationData.lines.forEach { item ->
            val title = context.resources.getQuantityString(notificationData.titleStringRes, 1)

            val notification = getDefaultNotificationBuilder(notificationData)
                .setContentTitle(title)
                .setContentText(item)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setSummaryText(student.nickOrName)
                        .bigText(item)
                )
                .setGroup(group)
                .build()

            notificationManager.notify(Random.nextInt(Int.MAX_VALUE), notification)

            saveNotification(title, item, notificationData, student)
        }
    }

    private fun MultipleNotificationsData.sendSummaryNotification(group: String, student: Student) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        val summaryNotification = getDefaultNotificationBuilder(this)
            .setSmallIcon(icon)
            .setContentTitle(getQuantityString(titleStringRes, lines.size))
            .setContentText(getQuantityString(contentStringRes, lines.size))
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText(student.nickOrName)
                    .also { builder -> lines.forEach { builder.addLine(it) } }
            )
            .setLocalOnly(true)
            .setGroup(group)
            .setGroupSummary(true)
            .build()

        val groupId = student.id * 100 + type.ordinal
        notificationManager.notify(groupId.toInt(), summaryNotification)
    }

    @SuppressLint("InlinedApi")
    private fun getDefaultNotificationBuilder(notificationData: NotificationData): NotificationCompat.Builder {
        val pendingIntentsFlags = if (appInfo.systemVersion >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return NotificationCompat.Builder(context, notificationData.type.channel)
            .setLargeIcon(context.getCompatBitmap(notificationData.icon, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_stat_all)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    notificationData.startMenu.id,
                    MainActivity.getStartIntent(context, notificationData.startMenu, true),
                    pendingIntentsFlags
                )
            )
    }

    private suspend fun saveNotification(
        title: String,
        content: String,
        notificationData: NotificationData,
        student: Student
    ) {
        val notificationEntity = Notification(
            studentId = student.id,
            title = title,
            content = content,
            type = notificationData.type,
            date = LocalDateTime.now()
        )

        notificationRepository.saveNotification(notificationEntity)
    }

    private fun getQuantityString(@PluralsRes res: Int, arg: Int): String {
        return context.resources.getQuantityString(res, arg, arg)
    }
}
