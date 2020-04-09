package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface StudentDao {

    @Insert(onConflict = ABORT)
    fun insertAll(student: List<Student>): List<Long>

    @Delete
    fun delete(student: Student)

    @Query("SELECT * FROM Students WHERE is_current = 1")
    fun loadCurrent(): Maybe<Student>

    @Query("SELECT * FROM Students WHERE id = :id")
    fun loadById(id: Int): Maybe<Student>

    @Query("SELECT * FROM Students")
    fun loadAll(): Maybe<List<Student>>

    @Query("UPDATE Students SET is_current = 1 WHERE id = :id")
    fun updateCurrent(id: Long)

    @Query("UPDATE Students SET is_current = 0")
    fun resetCurrent()
}
