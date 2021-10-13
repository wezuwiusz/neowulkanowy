package io.github.wulkanowy.services.alarm

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.N
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.HiltBroadcastReceiver
import io.github.wulkanowy.services.sync.channels.UpcomingLessonsChannel.Companion.CHANNEL_ID
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.toLocalDateTime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimetableNotificationReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    companion object {
        const val NOTIFICATION_TYPE_CURRENT = 1
        const val NOTIFICATION_TYPE_UPCOMING = 2
        const val NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION = 3

        const val NOTIFICATION_ID = "id"

        const val STUDENT_NAME = "student_name"
        const val STUDENT_ID = "student_id"
        const val LESSON_TYPE = "type"
        const val LESSON_TITLE = "title"
        const val LESSON_ROOM = "room"
        const val LESSON_NEXT_TITLE = "next_title"
        const val LESSON_NEXT_ROOM = "next_room"
        const val LESSON_START = "start_timestamp"
        const val LESSON_END = "end_timestamp"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("Receiving intent... ${intent.toUri(0)}")

        flowWithResource {
            val student = studentRepository.getCurrentStudent(false)
            val studentId = intent.getIntExtra(STUDENT_ID, 0)
            if (student.studentId == studentId) prepareNotification(context, intent)
            else Timber.d("Notification studentId($studentId) differs from current(${student.studentId})")
        }.onEach {
            if (it.status == Status.ERROR) Timber.e(it.error!!)
        }.launchIn(GlobalScope)
    }

    private fun prepareNotification(context: Context, intent: Intent) {
        val type = intent.getIntExtra(LESSON_TYPE, 0)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, MainView.Section.TIMETABLE.id)
        val isPersistent = preferencesRepository.isUpcomingLessonsNotificationsPersistent

        if (type == NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION) {
            return NotificationManagerCompat.from(context).cancel(notificationId)
        }

        val studentId = intent.getIntExtra(STUDENT_ID, 0)
        val studentName = intent.getStringExtra(STUDENT_NAME)

        val subject = intent.getStringExtra(LESSON_TITLE)
        val room = intent.getStringExtra(LESSON_ROOM)

        val start = intent.getLongExtra(LESSON_START, 0)
        val end = intent.getLongExtra(LESSON_END, 0)

        val nextSubject = intent.getStringExtra(LESSON_NEXT_TITLE)
        val nextRoom = intent.getStringExtra(LESSON_NEXT_ROOM)

        Timber.d("TimetableNotification receive: type: $type, subject: $subject, start: ${start.toLocalDateTime()}, student: $studentId")

        showNotification(context, notificationId, isPersistent, studentName,
            if (type == NOTIFICATION_TYPE_CURRENT) end else start, end - start,
            context.getString(
                if (type == NOTIFICATION_TYPE_CURRENT) R.string.timetable_now else R.string.timetable_next,
                "($room) $subject".removePrefix("()")
            ),
            nextSubject?.let {
                context.getString(
                    R.string.timetable_later,
                    "($nextRoom) $nextSubject".removePrefix("()")
                )
            }
        )
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        isPersistent: Boolean,
        studentName: String?,
        countDown: Long,
        timeout: Long,
        title: String,
        next: String?
    ) {
        NotificationManagerCompat.from(context)
            .notify(notificationId, NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(next)
                .setAutoCancel(false)
                .setWhen(countDown)
                .setOngoing(isPersistent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .apply {
                    if (Build.VERSION.SDK_INT >= N) setUsesChronometer(true)
                }
                .setTimeoutAfter(timeout)
                .setSmallIcon(R.drawable.ic_stat_timetable)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setStyle(NotificationCompat.InboxStyle().also {
                    it.setSummaryText(studentName)
                    it.addLine(next)
                })
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        MainView.Section.TIMETABLE.id,
                        MainActivity.getStartIntent(context, MainView.Section.TIMETABLE, true),
                        FLAG_UPDATE_CURRENT
                    )
                )
                .build()
            )
    }
}
