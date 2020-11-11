package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeSemesterStatisticsDao : BaseDao<GradeSemesterStatistics> {

    @Query("SELECT * FROM GradeSemesterStatistics WHERE student_id = :studentId AND semester_id = :semesterId")
    fun loadAll(semesterId: Int, studentId: Int): Flow<List<GradeSemesterStatistics>>
}
