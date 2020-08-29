package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.init
import java.time.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

@Singleton
class MessageRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getMessages(student: Student, semester: Semester, folder: MessageFolder): List<Message> {
        return sdk.init(student).getMessages(Folder.valueOf(folder.name), now().minusMonths(3), now()).map {
            Message(
                studentId = student.id.toInt(),
                realId = it.id ?: 0,
                messageId = it.messageId ?: 0,
                sender = it.sender?.name.orEmpty(),
                senderId = it.sender?.loginId ?: 0,
                recipient = it.recipients.singleOrNull()?.name ?: "Wielu adresatów",
                subject = it.subject.trim(),
                date = it.date ?: now(),
                folderId = it.folderId,
                unread = it.unread ?: false,
                unreadBy = it.unreadBy ?: 0,
                readBy = it.readBy ?: 0,
                removed = it.removed,
                hasAttachments = it.hasAttachments
            ).apply {
                content = it.content.orEmpty()
            }
        }
    }

    suspend fun getMessagesContentDetails(student: Student, message: Message, markAsRead: Boolean = false): Pair<String, List<MessageAttachment>> {
        return sdk.init(student).getMessageDetails(message.messageId, message.folderId, markAsRead, message.realId).let { details ->
            details.content to details.attachments.map {
                MessageAttachment(
                    realId = it.id,
                    messageId = it.messageId,
                    oneDriveId = it.oneDriveId,
                    url = it.url,
                    filename = it.filename
                )
            }
        }
    }

    suspend fun sendMessage(student: Student, subject: String, content: String, recipients: List<Recipient>): SentMessage {
        return sdk.init(student).sendMessage(
            subject = subject,
            content = content,
            recipients = recipients.map {
                SdkRecipient(
                    id = it.realId,
                    name = it.realName,
                    loginId = it.loginId,
                    reportingUnitId = it.unitId,
                    role = it.role,
                    hash = it.hash,
                    shortName = it.name
                )
            }
        )
    }

    suspend fun deleteMessage(student: Student, message: Message): Boolean {
        return sdk.init(student).deleteMessages(listOf(message.messageId), message.folderId)
    }
}
