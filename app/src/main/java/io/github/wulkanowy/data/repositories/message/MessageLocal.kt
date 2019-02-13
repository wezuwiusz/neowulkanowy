package io.github.wulkanowy.data.repositories.message

import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageLocal @Inject constructor(private val messagesDb: MessagesDao) {

    fun getMessage(studentId: Int, id: Int): Maybe<Message> {
        return messagesDb.loadOne(studentId, id)
    }

    fun getMessages(studentId: Int, folder: MessageRepository.MessageFolder): Maybe<List<Message>> {
        return when (folder) {
            MessageRepository.MessageFolder.TRASHED -> messagesDb.loadDeleted(studentId)
            else -> messagesDb.load(studentId, folder.id)
        }.filter { !it.isEmpty() }
    }

    fun getNewMessages(student: Student): Maybe<List<Message>> {
        return messagesDb.loadNewMessages(student.studentId)
    }

    fun saveMessages(messages: List<Message>): List<Long> {
        return messagesDb.insertAll(messages)
    }

    fun updateMessage(message: Message) {
        return messagesDb.update(message)
    }

    fun updateMessages(messages: List<Message>) {
        return messagesDb.updateAll(messages)
    }

    fun deleteMessages(messages: List<Message>) {
        messagesDb.deleteAll(messages)
    }
}
