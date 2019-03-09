package io.github.wulkanowy.services.sync.channels

import android.annotation.TargetApi
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import io.github.wulkanowy.R
import javax.inject.Inject

@TargetApi(26)
class NewEntriesChannel @Inject constructor(
    private val notificationManager: NotificationManager,
    private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "new_entries_channel"
    }

    fun create() {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_new_entries), IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = VISIBILITY_PUBLIC
            })
    }
}
