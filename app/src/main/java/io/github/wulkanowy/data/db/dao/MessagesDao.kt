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
    @Query("SELECT * FROM Messages WHERE message_global_key = :messageGlobalKey")
    fun loadMessageWithAttachment(messageGlobalKey: String): Flow<MessageWithAttachment?>

    @Query("SELECT * FROM Messages WHERE mailbox_key = :mailboxKey AND folder_id = :folder ORDER BY date DESC")
    fun loadAll(mailboxKey: String, folder: Int): Flow<List<Message>>

    @Query("SELECT * FROM Messages WHERE email = :email AND folder_id = :folder ORDER BY date DESC")
    fun loadAll(folder: Int, email: String): Flow<List<Message>>
}
