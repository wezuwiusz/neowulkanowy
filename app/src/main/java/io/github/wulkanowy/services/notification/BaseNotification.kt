package io.github.wulkanowy.services.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import timber.log.Timber
import kotlin.random.Random

abstract class BaseNotification(protected val context: Context) {

    protected val notificationManager: NotificationManager by lazy {
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun notify(notification: Notification) {
        notificationManager.notify(Random.nextInt(1000), notification)
    }

    fun notificationBuilder(channelId: String): NotificationCompat.Builder {
        if (SDK_INT >= O) createChannel(channelId)
        return NotificationCompat.Builder(context, channelId)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
        Timber.d("Notifications canceled")
    }

    abstract fun createChannel(channelId: String)
}
