package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.pojos.OneNotification
import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewLuckyNumberNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(item: LuckyNumber) {
        val notification = OneNotification(
            channelId = LuckyNumberChannel.CHANNEL_ID,
            icon = R.drawable.ic_stat_luckynumber,
            titleStringRes = R.string.lucky_number_notify_new_item_title,
            contentStringRes = R.string.lucky_number_notify_new_item,
            startMenu = MainView.Section.LUCKY_NUMBER,
            contentValues = listOf(item.luckyNumber.toString())
        )

        sendNotification(notification)
    }
}
