package io.github.wulkanowy.data.db.dao

import androidx.room.*
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import javax.inject.Singleton

@Singleton
@Dao
abstract class StudentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertAll(student: List<Student>): List<Long>

    @Delete
    abstract suspend fun delete(student: Student)

    @Update(entity = Student::class)
    abstract suspend fun update(studentNickAndAvatar: StudentNickAndAvatar)

    @Query("SELECT * FROM Students WHERE is_current = 1")
    abstract suspend fun loadCurrent(): Student?

    @Query("SELECT * FROM Students WHERE id = :id")
    abstract suspend fun loadById(id: Long): Student?

    @Query("SELECT * FROM Students")
    abstract suspend fun loadAll(): List<Student>

    @Transaction
    @Query("SELECT * FROM Students")
    abstract suspend fun loadStudentsWithSemesters(): List<StudentWithSemesters>

    @Transaction
    @Query("SELECT * FROM Students WHERE id = :id")
    abstract suspend fun loadStudentWithSemestersById(id: Long): StudentWithSemesters?

    @Query("UPDATE Students SET is_current = 1 WHERE id = :id")
    abstract suspend fun updateCurrent(id: Long)

    @Query("UPDATE Students SET is_current = 0")
    abstract suspend fun resetCurrent()

    @Transaction
    open suspend fun switchCurrent(id: Long) {
        resetCurrent()
        updateCurrent(id)
    }
}
