package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.uniqueSubtract
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val local: MessageLocal,
    private val remote: MessageRemote
) {

    suspend fun getMessages(student: Student, semester: Semester, folder: MessageFolder, forceRefresh: Boolean = false, notify: Boolean = false): List<Message> {
        return local.getMessages(student, folder).filter { !forceRefresh }.ifEmpty {
            val new = remote.getMessages(student, semester, folder)
            val old = local.getMessages(student, folder)

            local.deleteMessages(old.uniqueSubtract(new))
            local.saveMessages(new.uniqueSubtract(old).onEach {
                it.isNotified = !notify
            })

            local.getMessages(student, folder)
        }
    }

    suspend fun getMessage(student: Student, message: Message, markAsRead: Boolean = false): MessageWithAttachment {
        return local.getMessageWithAttachment(student, message).let {
            if (it.message.content.isNotEmpty().also { status ->
                    Timber.d("Message content in db empty: ${!status}")
                } && !it.message.unread) {
                return@let it
            }

            val dbMessage = local.getMessageWithAttachment(student, message)

            val (downloadedMessage, attachments) = remote.getMessagesContentDetails(student, dbMessage.message, markAsRead)

            local.updateMessages(listOf(dbMessage.message.copy(unread = !markAsRead).apply {
                id = dbMessage.message.id
                content = content.ifBlank { downloadedMessage }
            }))
            local.saveMessageAttachments(attachments)
            Timber.d("Message ${message.messageId} with blank content: ${dbMessage.message.content.isBlank()}, marked as read")

            local.getMessageWithAttachment(student, message)
        }
    }

    suspend fun getNotNotifiedMessages(student: Student): List<Message> {
        return local.getMessages(student, RECEIVED)
            .filter { message -> !message.isNotified && message.unread }
    }

    suspend fun updateMessages(messages: List<Message>) {
        return local.updateMessages(messages)
    }

    suspend fun sendMessage(student: Student, subject: String, content: String, recipients: List<Recipient>): SentMessage {
        return remote.sendMessage(student, subject, content, recipients)
    }

    suspend fun deleteMessage(student: Student, message: Message): Boolean {
        val delete = remote.deleteMessage(student, message)

        if (!message.removed) local.updateMessages(listOf(message.copy(removed = true).apply {
            id = message.id
            content = message.content
        }))
        else local.deleteMessages(listOf(message))

        return delete // TODO: wtf
    }
}
