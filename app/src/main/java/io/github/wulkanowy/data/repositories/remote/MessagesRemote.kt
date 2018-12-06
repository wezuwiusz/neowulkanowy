package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Single
import javax.inject.Inject
import io.github.wulkanowy.api.messages.Message as ApiMessage

class MessagesRemote @Inject constructor(private val api: Api) {

    fun getMessages(studentId: Int, folder: MessagesRepository.MessageFolder): Single<List<Message>> {
        return api.getMessages(Folder.valueOf(folder.name)).map { messages ->
            messages.map {
                Message(
                    studentId = studentId,
                    realId = it.id,
                    messageId = it.messageId,
                    sender = it.sender,
                    senderId = it.senderId,
                    recipient = it.recipient,
                    recipientId = it.recipientId,
                    subject = it.subject.trim(),
                    date = it.date?.toLocalDateTime(),
                    folderId = it.folderId,
                    unread = it.unread,
                    unreadBy = it.unreadBy,
                    readBy = it.readBy,
                    removed = it.removed
                )
            }
        }
    }

    fun getMessagesContent(message: Message, markAsRead: Boolean = false): Single<String> {
        return api.getMessageContent(message.messageId ?: 0, message.folderId, markAsRead, message.realId ?: 0)
    }
}
