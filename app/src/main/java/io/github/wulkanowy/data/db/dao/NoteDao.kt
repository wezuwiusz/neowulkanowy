package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface NoteDao : BaseDao<Note> {

    @Query("SELECT * FROM Notes WHERE student_id = :studentId")
    fun loadAll(studentId: Int): Flow<List<Note>>
}
