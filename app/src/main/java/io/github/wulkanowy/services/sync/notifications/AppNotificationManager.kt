package io.github.wulkanowy.services.sync.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.data.repositories.NotificationRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.PendingIntentCompat
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.nickOrName
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random

class AppNotificationManager @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @ApplicationContext private val context: Context,
    private val studentRepository: StudentRepository,
    private val notificationRepository: NotificationRepository
) {

    @SuppressLint("InlinedApi")
    suspend fun sendSingleNotification(
        notificationData: NotificationData,
        notificationType: NotificationType,
        student: Student
    ) {
        val notification = NotificationCompat.Builder(context, notificationType.channel)
            .setLargeIcon(context.getCompatBitmap(notificationType.icon, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_stat_all)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    Random.nextInt(),
                    SplashActivity.getStartIntent(context, notificationData.destination),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
            )
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationData.content)
                    .also { builder ->
                        if (!studentRepository.isOneUniqueStudent()) {
                            builder.setSummaryText(student.nickOrName)
                        }
                    }
            )
            .build()

        notificationManager.notify(Random.nextInt(), notification)
        saveNotification(notificationData, notificationType, student)
    }

    @SuppressLint("InlinedApi")
    suspend fun sendMultipleNotifications(
        groupNotificationData: GroupNotificationData,
        student: Student
    ) {
        val notificationType = groupNotificationData.type
        val groupType = notificationType.channel
        val group = "${groupType}_${student.id}"

        sendSummaryNotification(groupNotificationData, group, student)

        groupNotificationData.notificationDataList.forEach { notificationData ->
            val notification = NotificationCompat.Builder(context, notificationType.channel)
                .setLargeIcon(context.getCompatBitmap(notificationType.icon, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_stat_all)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        Random.nextInt(),
                        SplashActivity.getStartIntent(context, notificationData.destination),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )
                )
                .setContentTitle(notificationData.title)
                .setContentText(notificationData.content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(notificationData.content)
                        .also { builder ->
                            if (!studentRepository.isOneUniqueStudent()) {
                                builder.setSummaryText(student.nickOrName)
                            }
                        }
                )
                .setGroup(group)
                .build()

            notificationManager.notify(Random.nextInt(), notification)
            saveNotification(notificationData, groupNotificationData.type, student)
        }
    }

    private suspend fun sendSummaryNotification(
        groupNotificationData: GroupNotificationData,
        group: String,
        student: Student
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        val summaryNotification =
            NotificationCompat.Builder(context, groupNotificationData.type.channel)
                .setContentTitle(groupNotificationData.title)
                .setContentText(groupNotificationData.content)
                .setSmallIcon(groupNotificationData.type.icon)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .also { builder ->
                            if (!studentRepository.isOneUniqueStudent()) {
                                builder.setSummaryText(student.nickOrName)
                            }
                            groupNotificationData.notificationDataList.forEach {
                                builder.addLine(it.content)
                            }
                        }
                )
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        Random.nextInt(),
                        SplashActivity.getStartIntent(context, groupNotificationData.destination),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )
                )
                .setLocalOnly(true)
                .setGroup(group)
                .setGroupSummary(true)
                .build()

        val groupId = student.id * 100 + groupNotificationData.type.ordinal
        notificationManager.notify(groupId.toInt(), summaryNotification)
    }

    private suspend fun saveNotification(
        notificationData: NotificationData,
        notificationType: NotificationType,
        student: Student
    ) {
        val notificationEntity = Notification(
            studentId = student.id,
            title = notificationData.title,
            content = notificationData.content,
            destination = notificationData.destination,
            type = notificationType,
            date = Instant.now(),
        )

        notificationRepository.saveNotification(notificationEntity)
    }
}
