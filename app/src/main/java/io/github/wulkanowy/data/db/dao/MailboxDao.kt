package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Mailbox
import javax.inject.Singleton

@Singleton
@Dao
interface MailboxDao : BaseDao<Mailbox> {

    @Query("SELECT * FROM Mailboxes WHERE userLoginId = :userLoginId ")
    suspend fun loadAll(userLoginId: Int): List<Mailbox>
}
