package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.waitForResult
import io.github.wulkanowy.services.sync.notifications.NewMessageNotification
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MessageWork @Inject constructor(
    private val messageRepository: MessageRepository,
    private val newMessageNotification: NewMessageNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        val mailbox = messageRepository.getMailboxByStudent(student)
        messageRepository.getMessages(
            student = student,
            mailbox = mailbox,
            folder = RECEIVED,
            forceRefresh = true,
            notify = notify
        ).waitForResult()

        messageRepository.getMessagesFromDatabase(student, mailbox).first()
            .filter { !it.isNotified && it.unread }.let {
                if (it.isNotEmpty()) newMessageNotification.notify(it, student)
                messageRepository.updateMessages(it.onEach { message -> message.isNotified = true })
            }
    }
}
