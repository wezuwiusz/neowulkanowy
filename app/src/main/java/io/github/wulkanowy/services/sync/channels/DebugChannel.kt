package io.github.wulkanowy.services.sync.channels

import android.annotation.TargetApi
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

@TargetApi(26)
class DebugChannel @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @ApplicationContext private val context: Context,
    private val appInfo: AppInfo
) : Channel {

    companion object {
        const val CHANNEL_ID = "debug_channel"
    }

    override fun create() {
        if (!appInfo.isDebug) return
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_debug), IMPORTANCE_DEFAULT)
                .apply {
                    lockscreenVisibility = VISIBILITY_PUBLIC
                })
    }
}
