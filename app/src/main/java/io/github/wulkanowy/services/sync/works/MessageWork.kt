package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewMessageNotification
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MessageWork @Inject constructor(
    private val messageRepository: MessageRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newMessageNotification: NewMessageNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        messageRepository.getMessages(
            student = student,
            semester = semester,
            folder = RECEIVED,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        messageRepository.getMessagesFromDatabase(student).first()
            .filter { !it.isNotified && it.unread }.let {
                if (it.isNotEmpty()) newMessageNotification.notify(it)
                messageRepository.updateMessages(it.onEach { message -> message.isNotified = true })
            }
    }
}
