package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface GradePointsStatisticsDao : BaseDao<GradePointsStatistics> {

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId AND subject = :subjectName")
    fun loadSubject(semesterId: Int, studentId: Int, subjectName: String): Flow<List<GradePointsStatistics>>

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId")
    fun loadAll(semesterId: Int, studentId: Int): Flow<List<GradePointsStatistics>>
}
