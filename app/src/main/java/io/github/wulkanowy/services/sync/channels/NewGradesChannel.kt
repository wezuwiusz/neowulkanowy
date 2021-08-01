package io.github.wulkanowy.services.sync.channels

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import javax.inject.Inject

@TargetApi(26)
class NewGradesChannel @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    @ApplicationContext private val context: Context
) : Channel {

    companion object {
        const val CHANNEL_ID = "new_grade_channel"
        const val GROUP_DETAILS_ID = "new_grade_details_group"
        const val GROUP_PREDICTED_ID = "new_grade_predicted_group"
        const val GROUP_FINAL_ID = "new_grade_final_group"
    }

    override fun create() {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_new_grades), NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    enableLights(true)
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                })
    }
}
