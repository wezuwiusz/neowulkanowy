package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.AdminMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface AdminMessageDao : BaseDao<AdminMessage> {

    @Query("SELECT * FROM AdminMessages")
    fun loadAll(): Flow<List<AdminMessage>>
}
