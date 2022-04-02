package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.getPlural
import javax.inject.Inject

class NewNoteNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context
) {

    suspend fun notify(items: List<Note>, student: Student) {
        val notificationDataList = items.map {
            val titleRes = when (NoteCategory.getByValue(it.categoryType)) {
                NoteCategory.POSITIVE -> R.plurals.praise_new_items
                NoteCategory.NEUTRAL -> R.plurals.neutral_note_new_items
                else -> R.plurals.note_new_items
            }

            NotificationData(
                title = context.getPlural(titleRes, 1),
                content = "${it.teacher}: ${it.category}",
                destination = Destination.Note,
            )
        }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            destination = Destination.Note,
            title = context.getPlural(R.plurals.note_new_items, items.size),
            content = context.getPlural(R.plurals.note_notify_new_items, items.size, items.size),
            type = NotificationType.NEW_NOTE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }
}
