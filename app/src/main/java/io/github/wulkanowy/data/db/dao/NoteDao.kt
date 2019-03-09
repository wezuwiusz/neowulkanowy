package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.Note
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface NoteDao {

    @Insert
    fun insertAll(notes: List<Note>)

    @Update
    fun updateAll(notes: List<Note>)

    @Delete
    fun deleteAll(notes: List<Note>)

    @Query("SELECT * FROM Notes WHERE student_id = :studentId")
    fun loadAll(studentId: Int): Maybe<List<Note>>

}
