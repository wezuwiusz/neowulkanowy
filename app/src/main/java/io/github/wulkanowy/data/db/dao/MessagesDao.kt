package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Message
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface MessagesDao : BaseDao<Message> {

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND folder_id = :folder AND removed = 0 ORDER BY date DESC")
    fun loadAll(studentId: Int, folder: Int): Maybe<List<Message>>

    @Query("SELECT * FROM Messages WHERE id = :id")
    fun load(id: Long): Single<Message>

    @Query("SELECT * FROM Messages WHERE student_id = :studentId AND removed = 1 ORDER BY date DESC")
    fun loadDeleted(studentId: Int): Maybe<List<Message>>
}
