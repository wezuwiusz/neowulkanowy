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
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

@Singleton
class MessageRemote @Inject constructor(private val sdk: Sdk) {

    fun getMessages(student: Student, semester: Semester, folder: MessageFolder): Single<List<Message>> {
        return sdk.init(student).getMessages(Folder.valueOf(folder.name), semester.start.atStartOfDay(), semester.end.atStartOfDay()).map { messages ->
            messages.map {
                Message(
                    studentId = student.id.toInt(),
                    realId = it.id ?: 0,
                    messageId = it.messageId ?: 0,
                    sender = it.sender.orEmpty(),
                    senderId = it.senderId ?: 0,
                    recipient = it.recipient.orEmpty(),
                    subject = it.subject.trim(),
                    date = it.date ?: now(),
                    content = it.content.orEmpty(),
                    folderId = it.folderId,
                    unread = it.unread ?: false,
                    unreadBy = it.unreadBy ?: 0,
                    readBy = it.readBy ?: 0,
                    removed = it.removed,
                    hasAttachments = it.hasAttachments
                )
            }
        }
    }

    fun getMessagesContentDetails(student: Student, message: Message, markAsRead: Boolean = false): Single<Pair<String, List<MessageAttachment>>> {
        return sdk.init(student).getMessageDetails(message.messageId, message.folderId, markAsRead, message.realId).map { details ->
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

    fun sendMessage(student: Student, subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
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

    fun deleteMessage(student: Student, message: Message): Single<Boolean> {
        return sdk.init(student).deleteMessages(listOf(message.messageId to message.folderId))
    }
}
