package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.api.messages.SentMessage
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.api.messages.Message as ApiMessage
import io.github.wulkanowy.api.messages.Recipient as ApiRecipient

@Singleton
class MessageRemote @Inject constructor(private val api: Api) {

    fun getMessages(studentId: Int, folder: MessageRepository.MessageFolder): Single<List<Message>> {
        return api.getMessages(Folder.valueOf(folder.name)).map { messages ->
            messages.map {
                Message(
                    studentId = studentId,
                    realId = it.id ?: 0,
                    messageId = it.messageId ?: 0,
                    sender = it.sender.orEmpty(),
                    senderId = it.senderId ?: 0,
                    recipient = it.recipient.orEmpty(),
                    recipientId = it.recipientId,
                    subject = it.subject.trim(),
                    date = it.date?.toLocalDateTime() ?: now(),
                    folderId = it.folderId,
                    unread = it.unread ?: false,
                    unreadBy = it.unreadBy ?: 0,
                    readBy = it.readBy ?: 0,
                    removed = it.removed
                )
            }
        }
    }

    fun getMessagesContent(message: Message, markAsRead: Boolean = false): Single<String> {
        return api.getMessageContent(message.messageId, message.folderId, markAsRead, message.realId)
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return api.sendMessage(
            subject = subject,
            content = content,
            recipients = recipients.map {
                ApiRecipient(
                    id = it.realId,
                    realName = it.realName,
                    loginId = it.loginId,
                    reportingUnitId = it.unitId,
                    role = it.role,
                    hash = it.hash
                )
            }
        )
    }
}
