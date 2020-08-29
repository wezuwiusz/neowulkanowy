package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val local: MessageLocal,
    private val remote: MessageRemote
) {

    fun getMessages(student: Student, semester: Semester, folder: MessageFolder, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getMessages(student, folder) },
        fetch = { remote.getMessages(student, semester, folder) },
        saveFetchResult = { old, new ->
            local.deleteMessages(old uniqueSubtract new)
            local.saveMessages((new uniqueSubtract old).onEach {
                it.isNotified = !notify
            })
        }
    )

    fun getMessage(student: Student, message: Message, markAsRead: Boolean = false) = networkBoundResource(
        shouldFetch = {
            Timber.d("Message content in db empty: ${it.message.content.isEmpty()}")
            it.message.unread || it.message.content.isEmpty()
        },
        query = { local.getMessageWithAttachment(student, message) },
        fetch = { remote.getMessagesContentDetails(student, it.message, markAsRead) },
        saveFetchResult = { old, (downloadedMessage, attachments) ->
            local.updateMessages(listOf(old.message.copy(unread = !markAsRead).apply {
                id = old.message.id
                content = content.ifBlank { downloadedMessage }
            }))
            local.saveMessageAttachments(attachments)
            Timber.d("Message ${message.messageId} with blank content: ${old.message.content.isBlank()}, marked as read")
        }
    )

    fun getNotNotifiedMessages(student: Student): Flow<List<Message>> {
        return local.getMessages(student, RECEIVED).map { it.filter { message -> !message.isNotified && message.unread } }
    }

    suspend fun updateMessages(messages: List<Message>) {
        return local.updateMessages(messages)
    }

    suspend fun sendMessage(student: Student, subject: String, content: String, recipients: List<Recipient>): SentMessage {
        return remote.sendMessage(student, subject, content, recipients)
    }

    suspend fun deleteMessage(student: Student, message: Message) {
        val isDeleted = remote.deleteMessage(student, message)

        if (message.folderId != MessageFolder.TRASHED.id) {
            if (isDeleted) local.updateMessages(listOf(message.copy(folderId = MessageFolder.TRASHED.id).apply {
                id = message.id
                content = message.content
            }))
        } else local.deleteMessages(listOf(message))
    }
}
