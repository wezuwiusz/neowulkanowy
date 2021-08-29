package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.services.sync.channels.NewConferencesChannel
import io.github.wulkanowy.services.sync.channels.NewExamChannel
import io.github.wulkanowy.services.sync.channels.NewGradesChannel
import io.github.wulkanowy.services.sync.channels.NewHomeworkChannel
import io.github.wulkanowy.services.sync.channels.NewMessagesChannel
import io.github.wulkanowy.services.sync.channels.NewNotesChannel
import io.github.wulkanowy.services.sync.channels.NewSchoolAnnouncementsChannel

enum class NotificationType(val group: String, val channel: String) {
    NEW_CONFERENCE("new_conferences_group", NewConferencesChannel.CHANNEL_ID),
    NEW_EXAM("new_exam_group", NewExamChannel.CHANNEL_ID),
    NEW_GRADE_DETAILS("new_grade_details_group", NewGradesChannel.CHANNEL_ID),
    NEW_GRADE_PREDICTED("new_grade_predicted_group", NewGradesChannel.CHANNEL_ID),
    NEW_GRADE_FINAL("new_grade_final_group", NewGradesChannel.CHANNEL_ID),
    NEW_HOMEWORK("new_homework_group", NewHomeworkChannel.CHANNEL_ID),
    NEW_LUCKY_NUMBER("lucky_number_group", LuckyNumberChannel.CHANNEL_ID),
    NEW_MESSAGE("new_message_group", NewMessagesChannel.CHANNEL_ID),
    NEW_NOTE("new_notes_group", NewNotesChannel.CHANNEL_ID),
    NEW_ANNOUNCEMENT("new_school_announcements_group", NewSchoolAnnouncementsChannel.CHANNEL_ID),
}
