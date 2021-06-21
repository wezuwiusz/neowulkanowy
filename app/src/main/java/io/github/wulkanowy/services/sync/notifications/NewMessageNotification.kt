package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.pojos.MultipleNotifications
import io.github.wulkanowy.services.sync.channels.NewMessagesChannel
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewMessageNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationManager: NotificationManagerCompat,
) : BaseNotification(context, notificationManager) {

    fun notify(items: List<Message>) {
        val notification = MultipleNotifications(
            channelId = NewMessagesChannel.CHANNEL_ID,
            icon = R.drawable.ic_stat_message,
            titleStringRes = R.plurals.message_new_items,
            contentStringRes = R.plurals.message_notify_new_items,
            summaryStringRes = R.plurals.message_number_item,
            startMenu = MainView.Section.MESSAGE,
            lines = items.map {
                "${it.sender}: ${it.subject}"
            }
        )

        sendNotification(notification)
    }
}
