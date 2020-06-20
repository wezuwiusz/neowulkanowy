package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import javax.inject.Singleton

@Singleton
@Dao
interface GradePointsStatisticsDao : BaseDao<GradePointsStatistics> {

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId AND subject = :subjectName")
    suspend fun loadSubject(semesterId: Int, studentId: Int, subjectName: String): List<GradePointsStatistics>

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId")
    suspend fun loadAll(semesterId: Int, studentId: Int): List<GradePointsStatistics>
}
