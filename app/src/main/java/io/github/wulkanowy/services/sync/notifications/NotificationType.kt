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
    val channel: String,
    val icon: Int
) {
    NEW_CONFERENCE(
        channel = NewConferencesChannel.CHANNEL_ID,
        icon = R.drawable.ic_more_conferences,
    ),
    NEW_EXAM(
        channel = NewExamChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_exam
    ),
    NEW_GRADE_DETAILS(
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_GRADE_PREDICTED(
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_GRADE_FINAL(
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_GRADE_DESCRIPTIVE(
        channel = NewGradesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_grade,
    ),
    NEW_HOMEWORK(
        channel = NewHomeworkChannel.CHANNEL_ID,
        icon = R.drawable.ic_more_homework,
    ),
    NEW_LUCKY_NUMBER(
        channel = LuckyNumberChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_luckynumber,
    ),
    NEW_MESSAGE(
        channel = NewMessagesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_message,
    ),
    NEW_NOTE(
        channel = NewNotesChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_note
    ),
    NEW_ANNOUNCEMENT(
        channel = NewSchoolAnnouncementsChannel.CHANNEL_ID,
        icon = R.drawable.ic_all_about
    ),
    CHANGE_TIMETABLE(
        channel = TimetableChangeChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_timetable
    ),
    NEW_ATTENDANCE(
        channel = NewAttendanceChannel.CHANNEL_ID,
        icon = R.drawable.ic_main_attendance
    ),
    PUSH(
        channel = PushChannel.CHANNEL_ID,
        icon = R.drawable.ic_stat_all
    )
}
