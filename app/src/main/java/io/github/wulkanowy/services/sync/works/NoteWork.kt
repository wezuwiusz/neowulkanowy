package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewNotesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject
import kotlin.random.Random

class NoteWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxSingle { noteRepository.getNotes(student, semester, true, preferencesRepository.isNotificationsEnable).waitForResult() }
            .flatMap { rxSingle { noteRepository.getNotNotifiedNotes(student).first() } }
            .flatMapCompletable {
                if (it.isNotEmpty()) notify(it)
                rxCompletable { noteRepository.updateNotes(it.onEach { note -> note.isNotified = true }) }
            }
    }

    private fun notify(notes: List<Note>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(context, NewNotesChannel.CHANNEL_ID)
            .setContentTitle(context.resources.getQuantityString(R.plurals.note_new_items, notes.size, notes.size))
            .setContentText(context.resources.getQuantityString(R.plurals.note_notify_new_items, notes.size, notes.size))
            .setSmallIcon(R.drawable.ic_stat_note)
            .setAutoCancel(true)
            .setDefaults(DEFAULT_ALL)
            .setPriority(PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, MainView.Section.NOTE.id,
                    MainActivity.getStartIntent(context, MainView.Section.NOTE, true), FLAG_UPDATE_CURRENT))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.note_number_item, notes.size, notes.size))
                notes.forEach { addLine("${it.teacher}: ${it.category}") }
                this
            })
            .build())
    }
}

