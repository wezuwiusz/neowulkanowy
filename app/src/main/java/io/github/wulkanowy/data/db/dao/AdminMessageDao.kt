package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.wulkanowy.data.db.entities.AdminMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
abstract class AdminMessageDao : BaseDao<AdminMessage> {

    @Query("SELECT * FROM AdminMessages")
    abstract fun loadAll(): Flow<List<AdminMessage>>

    @Transaction
    open suspend fun removeOldAndSaveNew(
        oldMessages: List<AdminMessage>,
        newMessages: List<AdminMessage>
    ) {
        deleteAll(oldMessages)
        insertAll(newMessages)
    }
}