package io.github.wulkanowy.services.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.N
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.services.sync.channels.UpcomingLessonsChannel.Companion.CHANNEL_ID
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.PendingIntentCompat
import io.github.wulkanowy.utils.getCompatColor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimetableNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    companion object {
        const val NOTIFICATION_TYPE_CURRENT = 1
        const val NOTIFICATION_TYPE_UPCOMING = 2
        const val NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION = 3

        // FIXME only shows one notification even if there are multiple students.
        //       Probably want to fix after #721 is merged.
        const val NOTIFICATION_ID = 2137

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
        Timber.d("Receiving intent... ${intent.toUri(0)}")

        resourceFlow {
            val showStudentName = !studentRepository.isOneUniqueStudent()
            val student = studentRepository.getCurrentStudent(false)
            val studentId = intent.getIntExtra(STUDENT_ID, 0)

            if (student.studentId == studentId) {
                prepareNotification(context, intent, showStudentName)
            } else {
                Timber.d("Notification studentId($studentId) differs from current(${student.studentId})")
            }
        }
            .onResourceError { Timber.e(it) }
            .launchIn(GlobalScope)
    }

    private fun prepareNotification(context: Context, intent: Intent, showStudentName: Boolean) {
        val type = intent.getIntExtra(LESSON_TYPE, 0)
        val isPersistent = preferencesRepository.isUpcomingLessonsNotificationsPersistent

        if (type == NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION) {
            return NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }

        val studentId = intent.getIntExtra(STUDENT_ID, 0)
        val studentName = intent.getStringExtra(STUDENT_NAME).takeIf { showStudentName }

        val subject = intent.getStringExtra(LESSON_TITLE)
        val room = intent.getStringExtra(LESSON_ROOM)

        val start = intent.getLongExtra(LESSON_START, 0)
        val end = intent.getLongExtra(LESSON_END, 0)

        val nextSubject = intent.getStringExtra(LESSON_NEXT_TITLE)
        val nextRoom = intent.getStringExtra(LESSON_NEXT_ROOM)

        Timber.d("TimetableNotification receive: type: $type, subject: $subject, start: $start, student: $studentId")

        val notificationTitleResId =
            if (type == NOTIFICATION_TYPE_CURRENT) R.string.timetable_now else R.string.timetable_next
        val notificationTitle =
            context.getString(notificationTitleResId, "($room) $subject".removePrefix("()"))

        val nextLessonText = nextSubject?.let {
            context.getString(
                R.string.timetable_later,
                "($nextRoom) $nextSubject".removePrefix("()")
            )
        }

        showNotification(
            context = context,
            isPersistent = isPersistent,
            studentName = studentName,
            countDown = if (type == NOTIFICATION_TYPE_CURRENT) end else start,
            timeout = end - start,
            title = notificationTitle,
            next = nextLessonText
        )
    }

    private fun showNotification(
        context: Context,
        isPersistent: Boolean,
        studentName: String?,
        countDown: Long,
        timeout: Long,
        title: String,
        next: String?
    ) {
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, NotificationCompat.Builder(context, CHANNEL_ID)
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
                .setStyle(NotificationCompat.InboxStyle()
                    .addLine(next)
                    .also { inboxStyle ->
                        studentName?.let { inboxStyle.setSummaryText(it) }
                    })
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        NOTIFICATION_ID,
                        SplashActivity.getStartIntent(context, Destination.Timetable()),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )
                )
                .build()
            )
    }
}
