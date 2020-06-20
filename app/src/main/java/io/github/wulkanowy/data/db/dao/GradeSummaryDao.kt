package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradeSummary
import javax.inject.Singleton

@Singleton
@Dao
interface GradeSummaryDao : BaseDao<GradeSummary> {

    @Query("SELECT * FROM GradesSummary WHERE student_id = :studentId AND semester_id = :semesterId")
    suspend fun loadAll(semesterId: Int, studentId: Int): List<GradeSummary>
}
