package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewNoteNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

    suspend fun notify(items: List<Note>, student: Student) {
        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_NOTE,
            icon = R.drawable.ic_stat_note,
            titleStringRes = when (NoteCategory.getByValue(items.first().categoryType)) {
                NoteCategory.POSITIVE -> R.plurals.praise_new_items
                NoteCategory.NEUTRAL -> R.plurals.neutral_note_new_items
                else -> R.plurals.note_new_items
            },
            contentStringRes = when (NoteCategory.getByValue(items.first().categoryType)) {
                NoteCategory.POSITIVE -> R.plurals.praise_notify_new_items
                NoteCategory.NEUTRAL -> R.plurals.neutral_note_notify_new_items
                else -> R.plurals.note_notify_new_items
            },
            summaryStringRes = when (NoteCategory.getByValue(items.first().categoryType)) {
                NoteCategory.POSITIVE -> R.plurals.praise_number_item
                NoteCategory.NEUTRAL -> R.plurals.neutral_note_number_item
                else -> R.plurals.note_number_item
            },
            startMenu = MainView.Section.NOTE,
            lines = items.map {
                "${it.teacher}: ${it.category}"
            }
        )

        appNotificationManager.sendNotification(notification, student)
    }
}
