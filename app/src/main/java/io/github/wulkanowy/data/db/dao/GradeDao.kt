package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Grade
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface GradeDao : BaseDao<Grade> {

    @Query("SELECT * FROM Grades WHERE semester_id = :semesterId AND student_id = :studentId")
    fun loadAll(semesterId: Int, studentId: Int): Flow<List<Grade>>
}
