package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Recipient
import javax.inject.Singleton

@Singleton
@Dao
interface RecipientDao : BaseDao<Recipient> {

    @Query("SELECT * FROM Recipients WHERE student_id = :studentId AND role = :role AND unit_id = :unitId")
    suspend fun loadAll(studentId: Int, role: Int, unitId: Int): List<Recipient>
}
