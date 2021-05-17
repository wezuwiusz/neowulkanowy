package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewConferencesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

class ConferenceWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val conferenceRepository: ConferenceRepository,
    private val notificationManager: NotificationManagerCompat,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        conferenceRepository.getConferences(
            student = student,
            semester = semester,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        conferenceRepository.getNotNotifiedConference(semester).first().let {
            if (it.isNotEmpty()) notify(it)

            conferenceRepository.updateConference(it.onEach { conference -> conference.isNotified = true })
        }
    }

    private fun notify(conference: List<Conference>) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewConferencesChannel.CHANNEL_ID)
                .setContentTitle(context.resources.getQuantityString(R.plurals.conference_notify_new_item_title, conference.size, conference.size))
                .setContentText(context.resources.getQuantityString(R.plurals.conference_notify_new_items, conference.size, conference.size))
                .setSmallIcon(R.drawable.ic_stat_all)
                .setLargeIcon(context.getCompatBitmap(R.drawable.ic_more_conferences, R.color.colorPrimary))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, MainView.Section.CONFERENCE.id,
                        MainActivity.getStartIntent(context, MainView.Section.CONFERENCE, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    setSummaryText(
                        context.resources.getQuantityString(
                            R.plurals.conference_number_item,
                            conference.size,
                            conference.size
                        )
                    )
                    conference.forEach { addLine("${it.title}: ${it.subject}") }
                    this
                })
                .build()
        )
    }
}