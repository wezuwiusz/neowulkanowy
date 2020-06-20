package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Grade
import javax.inject.Singleton

@Singleton
@Dao
interface GradeDao : BaseDao<Grade> {

    @Query("SELECT * FROM Grades WHERE semester_id = :semesterId AND student_id = :studentId")
    suspend fun loadAll(semesterId: Int, studentId: Int): List<Grade>
}
