package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Semester
import javax.inject.Singleton

@Singleton
@Dao
interface SemesterDao : BaseDao<Semester> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSemesters(items: List<Semester>): List<Long>

    @Query("SELECT * FROM Semesters WHERE (student_id = :studentId AND class_id = :classId) OR (student_id = :studentId AND class_id = 0)")
    suspend fun loadAll(studentId: Int, classId: Int): List<Semester>
}
