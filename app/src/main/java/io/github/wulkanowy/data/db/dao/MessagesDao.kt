package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao : BaseDao<Message> {

    @Transaction
    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND message_id = :messageId")
    fun loadMessageWithAttachment(studentId: Int, messageId: Int): Flow<MessageWithAttachment?>

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND folder_id = :folder ORDER BY date DESC")
    fun loadAll(studentId: Int, folder: Int): Flow<List<Message>>
}
