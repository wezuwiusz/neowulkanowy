package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradeDescriptive
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface GradeDescriptiveDao : BaseDao<GradeDescriptive> {

    @Query("SELECT * FROM GradesDescriptive WHERE semester_id = :semesterId AND student_id = :studentId")
    fun loadAll(semesterId: Int, studentId: Int): Flow<List<GradeDescriptive>>
}
