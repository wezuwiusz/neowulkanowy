package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment

@Dao
interface MessagesDao : BaseDao<Message> {

    @Transaction
    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND message_id = :messageId")
    suspend fun loadMessageWithAttachment(studentId: Int, messageId: Int): MessageWithAttachment

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND folder_id = :folder AND removed = 0 ORDER BY date DESC")
    suspend fun loadAll(studentId: Int, folder: Int): List<Message>

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND removed = 1 ORDER BY date DESC")
    suspend fun loadDeleted(studentId: Int): List<Message>
}
