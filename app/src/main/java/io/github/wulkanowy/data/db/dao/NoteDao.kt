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
    fun update(note: Note)

    @Update
    fun updateAll(notes: List<Note>)

    @Delete
    fun deleteAll(notes: List<Note>)

    @Query("SELECT * FROM Notes WHERE semester_id = :semesterId AND student_id = :studentId")
    fun load(semesterId: Int, studentId: Int): Maybe<List<Note>>

    @Query("SELECT * FROM Notes WHERE is_read = 0 AND semester_id = :semesterId AND student_id = :studentId")
    fun loadNew(semesterId: Int, studentId: Int): Maybe<List<Note>>
}
