package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.NoteRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory.NEUTRAL
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory.POSITIVE
import io.github.wulkanowy.services.sync.channels.NewNotesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

class NoteWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        noteRepository.getNotes(student, semester, true, preferencesRepository.isNotificationsEnable).waitForResult()

        noteRepository.getNotNotifiedNotes(student).first().let {
            if (it.isNotEmpty()) notify(it)
            noteRepository.updateNotes(it.onEach { note -> note.isNotified = true })
        }
    }

    private fun notify(notes: List<Note>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(context, NewNotesChannel.CHANNEL_ID)
            .setContentTitle(
                when (NoteCategory.getByValue(notes.first().categoryType)) {
                    POSITIVE -> context.resources.getQuantityString(R.plurals.praise_new_items, notes.size, notes.size)
                    NEUTRAL -> context.resources.getQuantityString(R.plurals.neutral_note_new_items, notes.size, notes.size)
                    else -> context.resources.getQuantityString(R.plurals.note_new_items, notes.size, notes.size)
                }
            )
            .setContentText(
                when (NoteCategory.getByValue(notes.first().categoryType)) {
                    POSITIVE -> context.resources.getQuantityString(R.plurals.praise_notify_new_items, notes.size, notes.size)
                    NEUTRAL -> context.resources.getQuantityString(R.plurals.neutral_note_notify_new_items, notes.size, notes.size)
                    else -> context.resources.getQuantityString(R.plurals.note_notify_new_items, notes.size, notes.size)
                }
            )
            .setSmallIcon(R.drawable.ic_stat_all)
            .setLargeIcon(
                context.getCompatBitmap(R.drawable.ic_stat_note, R.color.colorPrimary)
            )
            .setAutoCancel(true)
            .setDefaults(DEFAULT_ALL)
            .setPriority(PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, MainView.Section.NOTE.id,
                    MainActivity.getStartIntent(context, MainView.Section.NOTE, true), FLAG_UPDATE_CURRENT))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(
                    when (NoteCategory.getByValue(notes.first().categoryType)) {
                        POSITIVE -> context.resources.getQuantityString(R.plurals.praise_number_item, notes.size, notes.size)
                        NEUTRAL -> context.resources.getQuantityString(R.plurals.neutral_note_number_item, notes.size, notes.size)
                        else -> context.resources.getQuantityString(R.plurals.note_number_item, notes.size, notes.size)
                    }
                )
                notes.forEach { addLine("${it.teacher}: ${it.category}") }
                this
            })
            .build())
    }
}

