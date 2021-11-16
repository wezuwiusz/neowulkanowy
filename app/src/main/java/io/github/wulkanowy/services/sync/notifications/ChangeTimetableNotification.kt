package io.github.wulkanowy.services.sync.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.pojos.GroupNotificationData
import io.github.wulkanowy.data.pojos.NotificationData
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.getPlural
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class ChangeTimetableNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager,
    @ApplicationContext private val context: Context,
) {

    suspend fun notify(items: List<Timetable>, student: Student) {
        val currentTime = LocalDateTime.now()
        val changedLessons = items.filter { (it.canceled || it.changes) && it.start > currentTime }
        val notificationDataList = changedLessons.groupBy { it.date }
            .map { (date, lessons) ->
                getNotificationContents(date, lessons).map {
                    NotificationData(
                        title = context.getPlural(
                            R.plurals.timetable_notify_new_items_title,
                            1
                        ),
                        content = it,
                        intentToStart = SplashActivity.getStartIntent(
                            context = context,
                            destination = Destination.Timetable(date)
                        )
                    )
                }
            }
            .flatten()
            .ifEmpty { return }

        val groupNotificationData = GroupNotificationData(
            notificationDataList = notificationDataList,
            title = context.getPlural(
                R.plurals.timetable_notify_new_items_title,
                changedLessons.size
            ),
            content = context.getPlural(
                R.plurals.timetable_notify_new_items_group,
                changedLessons.size,
                changedLessons.size
            ),
            intentToStart = SplashActivity.getStartIntent(context, Destination.Timetable()),
            type = NotificationType.CHANGE_TIMETABLE
        )

        appNotificationManager.sendMultipleNotifications(groupNotificationData, student)
    }

    private fun getNotificationContents(date: LocalDate, lessons: List<Timetable>): List<String> {
        val formattedDate = date.toFormattedString("EEE dd.MM")

        return if (lessons.size > 2) {
            listOf(
                context.getPlural(
                    R.plurals.timetable_notify_new_items,
                    lessons.size,
                    formattedDate,
                    lessons.size,
                )
            )
        } else {
            lessons.map {
                buildString {
                    append(
                        context.getString(
                            R.string.timetable_notify_lesson,
                            formattedDate,
                            it.number,
                            it.subject
                        )
                    )
                    if (it.roomOld.isNotBlank()) {
                        appendLine()
                        append(
                            context.getString(
                                R.string.timetable_notify_change_room,
                                it.roomOld,
                                it.room
                            )
                        )
                    }
                    if (it.teacherOld.isNotBlank() && it.teacher != it.teacherOld) {
                        appendLine()
                        append(
                            context.getString(
                                R.string.timetable_notify_change_teacher,
                                it.teacherOld,
                                it.teacher
                            )
                        )
                    }
                    if (it.subjectOld.isNotBlank()) {
                        appendLine()
                        append(
                            context.getString(
                                R.string.timetable_notify_change_subject,
                                it.subjectOld,
                                it.subject
                            )
                        )
                    }
                    if (it.info.isNotBlank()) {
                        appendLine()
                        append(it.info)
                    }
                }
            }
        }
    }
}
