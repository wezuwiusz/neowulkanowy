package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import androidx.room.Transaction
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import javax.inject.Singleton

@Singleton
@Dao
interface StudentDao {

    @Insert(onConflict = ABORT)
    suspend fun insertAll(student: List<Student>): List<Long>

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM Students WHERE is_current = 1")
    suspend fun loadCurrent(): Student?

    @Query("SELECT * FROM Students WHERE id = :id")
    suspend fun loadById(id: Int): Student?

    @Query("SELECT * FROM Students")
    suspend fun loadAll(): List<Student>

    @Transaction
    @Query("SELECT * FROM Students")
    suspend fun loadStudentsWithSemesters(): List<StudentWithSemesters>

    @Query("UPDATE Students SET is_current = 1 WHERE id = :id")
    suspend fun updateCurrent(id: Long)

    @Query("UPDATE Students SET is_current = 0")
    suspend fun resetCurrent()
}
