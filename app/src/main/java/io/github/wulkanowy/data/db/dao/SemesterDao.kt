package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Singleton

@Singleton
@Dao
interface SemesterDao : BaseDao<Semester> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSemesters(items: List<Semester>): List<Long>

    @Query("SELECT * FROM Semesters WHERE student_id = :studentId AND class_id = :classId")
    suspend fun loadAll(studentId: Int, classId: Int): List<Semester>

    @Query("SELECT * FROM Semesters WHERE student_id = :studentId")
    suspend fun loadAll(studentId: Int): List<Semester>

    suspend fun loadAll(student: Student): List<Semester> {
        return if (student.isEduOne == true) {
            loadAll(student.studentId)
        } else {
            loadAll(student.studentId, student.classId)
        }
    }
}
