package io.github.wulkanowy.services.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_END
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_NEXT_ROOM
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_NEXT_TITLE
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_ROOM
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_START
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_TITLE
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.LESSON_TYPE
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.NOTIFICATION_ID
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.NOTIFICATION_TYPE_CURRENT
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.NOTIFICATION_TYPE_UPCOMING
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.STUDENT_ID
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver.Companion.STUDENT_NAME
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.nickOrName
import io.github.wulkanowy.utils.toTimestamp
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.inject.Inject

class TimetableNotificationSchedulerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val preferencesRepository: PreferencesRepository,
    private val dispatchersProvider: DispatchersProvider,
) {

    private fun getRequestCode(time: LocalDateTime, studentId: Int) =
        (time.toTimestamp() * studentId).toInt()

    private fun getUpcomingLessonTime(
        index: Int,
        day: List<Timetable>,
        lesson: Timetable
    ) = day.getOrNull(index - 1)?.end ?: lesson.start.minusMinutes(30)

    suspend fun cancelScheduled(lessons: List<Timetable>, student: Student) {
        val studentId = student.studentId
        withContext(dispatchersProvider.backgroundThread) {
            lessons.sortedBy { it.start }.forEachIndexed { index, lesson ->
                val upcomingTime = getUpcomingLessonTime(index, lessons, lesson)
                cancelScheduledTo(
                    upcomingTime..lesson.start,
                    getRequestCode(upcomingTime, studentId)
                )
                cancelScheduledTo(lesson.start..lesson.end, getRequestCode(lesson.start, studentId))

                Timber.d("TimetableNotification canceled: type 1 & 2, subject: ${lesson.subject}, start: ${lesson.start}, student: $studentId")
            }
        }
    }

    private fun cancelScheduledTo(range: ClosedRange<LocalDateTime>, requestCode: Int) {
        if (now() in range) cancelNotification()
        alarmManager.cancel(
            PendingIntent.getBroadcast(context, requestCode, Intent(), FLAG_UPDATE_CURRENT)
        )
    }

    fun cancelNotification() =
        NotificationManagerCompat.from(context).cancel(MainView.Section.TIMETABLE.id)

    suspend fun scheduleNotifications(lessons: List<Timetable>, student: Student) {
        if (!preferencesRepository.isUpcomingLessonsNotificationsEnable) {
            return cancelScheduled(lessons, student)
        }

        withContext(dispatchersProvider.backgroundThread) {
            lessons.groupBy { it.date }
                .map { it.value.sortedBy { lesson -> lesson.start } }
                .map { it.filter { lesson -> lesson.isStudentPlan } }
                .map { day ->
                    val canceled = day.filter { it.canceled }
                    val active = day.filter { !it.canceled }

                    cancelScheduled(canceled, student)
                    active.forEachIndexed { index, lesson ->
                        val intent = createIntent(student, lesson, active.getOrNull(index + 1))

                        if (lesson.start > now()) {
                            scheduleBroadcast(
                                intent,
                                student.studentId,
                                NOTIFICATION_TYPE_UPCOMING,
                                getUpcomingLessonTime(index, active, lesson)
                            )
                        }

                        if (lesson.end > now()) {
                            scheduleBroadcast(
                                intent,
                                student.studentId,
                                NOTIFICATION_TYPE_CURRENT,
                                lesson.start
                            )
                            if (active.lastIndex == index) {
                                scheduleBroadcast(
                                    intent,
                                    student.studentId,
                                    NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION,
                                    lesson.end
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun createIntent(student: Student, lesson: Timetable, nextLesson: Timetable?): Intent {
        return Intent(context, TimetableNotificationReceiver::class.java).apply {
            putExtra(STUDENT_ID, student.studentId)
            putExtra(STUDENT_NAME, student.nickOrName)
            putExtra(LESSON_ROOM, lesson.room)
            putExtra(LESSON_START, lesson.start.toTimestamp())
            putExtra(LESSON_END, lesson.end.toTimestamp())
            putExtra(LESSON_TITLE, lesson.subject)
            putExtra(LESSON_NEXT_TITLE, nextLesson?.subject)
            putExtra(LESSON_NEXT_ROOM, nextLesson?.room)
        }
    }

    private fun scheduleBroadcast(
        intent: Intent,
        studentId: Int,
        notificationType: Int,
        time: LocalDateTime
    ) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager, RTC_WAKEUP, time.toTimestamp(),
            PendingIntent.getBroadcast(context, getRequestCode(time, studentId), intent.also {
                it.putExtra(NOTIFICATION_ID, MainView.Section.TIMETABLE.id)
                it.putExtra(LESSON_TYPE, notificationType)
            }, FLAG_UPDATE_CURRENT)
        )
        Timber.d(
            "TimetableNotification scheduled: type: $notificationType, subject: ${
                intent.getStringExtra(LESSON_TITLE)
            }, start: $time, student: $studentId"
        )
    }
}
