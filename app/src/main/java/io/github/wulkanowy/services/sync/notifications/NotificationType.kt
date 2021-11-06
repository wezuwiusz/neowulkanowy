package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.services.sync.channels.NewAttendanceChannel
import io.github.wulkanowy.services.sync.channels.NewConferencesChannel
import io.github.wulkanowy.services.sync.channels.NewExamChannel
import io.github.wulkanowy.services.sync.channels.NewGradesChannel
import io.github.wulkanowy.services.sync.channels.NewHomeworkChannel
import io.github.wulkanowy.services.sync.channels.NewMessagesChannel
import io.github.wulkanowy.services.sync.channels.NewNotesChannel
import io.github.wulkanowy.services.sync.channels.NewSchoolAnnouncementsChannel
import io.github.wulkanowy.services.sync.channels.PushChannel
import io.github.wulkanowy.services.sync.channels.TimetableChangeChannel

enum class NotificationType(
    val group: String?,
    val channel: String,
    val icon: Int
) {
    NEW_CONFERENCE(
        group = "new_conferences_group",
        channel = NewConferencesChannel.CHANNEL_ID,
        icon = R.drawable.ic_more_conferences,
    ),
    NEW_EXAM(
        group = "new_exam_group",
        channel = NewExamChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_exam
    ),
    NEW_GRADE_DETAILS(
        group = "new_grade_details_group",
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_GRADE_PREDICTED(
        group = "new_grade_predicted_group",
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_GRADE_FINAL(
        group = "new_grade_final_group",
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_HOMEWORK(
        group = "new_homework_group",
        channel = NewHomeworkChannel.CHANNEL_ID,
        icon = R.drawable.ic_more_homework,
    ),
    NEW_LUCKY_NUMBER(
        group = null,
        channel = LuckyNumberChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_luckynumber,
    ),
    NEW_MESSAGE(
        group = "new_message_group",
        channel = NewMessagesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_message,
    ),
    NEW_NOTE(
        group = "new_notes_group",
        channel = NewNotesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_note
    ),
    NEW_ANNOUNCEMENT(
        group = "new_school_announcements_group",
        channel = NewSchoolAnnouncementsChannel.CHANNEL_ID,
        icon = R.drawable.ic_all_about
    ),
    CHANGE_TIMETABLE(
        group = "change_timetable_group",
        channel = TimetableChangeChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_timetable
    ),
    NEW_ATTENDANCE(
        group = "new_attendance_group",
        channel = NewAttendanceChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_attendance
    ),
    PUSH(
        group = null,
        channel = PushChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_all
    )
}
