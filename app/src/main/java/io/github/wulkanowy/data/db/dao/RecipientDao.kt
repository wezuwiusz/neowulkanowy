package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Recipient
import javax.inject.Singleton

@Singleton
@Dao
interface RecipientDao : BaseDao<Recipient> {

    @Query("SELECT * FROM Recipients WHERE student_id = :userLoginId AND unit_id = :unitId AND role = :role")
    suspend fun loadAll(userLoginId: Int, unitId: Int, role: Int): List<Recipient>
}
