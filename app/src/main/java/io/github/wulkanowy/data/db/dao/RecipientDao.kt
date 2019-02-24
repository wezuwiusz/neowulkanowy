package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Recipient
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface RecipientDao {

    @Insert
    fun insertAll(messages: List<Recipient>)

    @Delete
    fun deleteAll(messages: List<Recipient>)

    @Query("SELECT * FROM Recipients WHERE student_id = :studentId AND role = :role AND unit_id = :unitId")
    fun load(studentId: Int, role: Int, unitId: Int): Maybe<List<Recipient>>
}
