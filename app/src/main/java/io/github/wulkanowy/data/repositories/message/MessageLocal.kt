package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.TRASHED
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageLocal @Inject constructor(
    private val messagesDb: MessagesDao,
    private val messageAttachmentDao: MessageAttachmentDao
) {

    fun saveMessages(messages: List<Message>) {
        messagesDb.insertAll(messages)
    }

    fun updateMessages(messages: List<Message>) {
        messagesDb.updateAll(messages)
    }

    fun deleteMessages(messages: List<Message>) {
        messagesDb.deleteAll(messages)
    }

    fun getMessageWithAttachment(student: Student, message: Message): Single<MessageWithAttachment> {
        return messagesDb.loadMessageWithAttachment(student.id.toInt(), message.messageId)
    }

    fun saveMessageAttachments(attachments: List<MessageAttachment>) {
        messageAttachmentDao.insertAttachments(attachments)
    }

    fun getMessages(student: Student, folder: MessageFolder): Maybe<List<Message>> {
        return when (folder) {
            TRASHED -> messagesDb.loadDeleted(student.id.toInt())
            else -> messagesDb.loadAll(student.id.toInt(), folder.id)
        }.filter { it.isNotEmpty() }
    }
}
