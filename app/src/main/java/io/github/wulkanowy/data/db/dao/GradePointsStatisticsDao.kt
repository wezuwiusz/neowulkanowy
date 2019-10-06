package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface GradePointsStatisticsDao {

    @Insert
    fun insertAll(gradesStatistics: List<GradePointsStatistics>)

    @Delete
    fun deleteAll(gradesStatistics: List<GradePointsStatistics>)

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId AND subject = :subjectName")
    fun loadSubject(semesterId: Int, studentId: Int, subjectName: String): Maybe<GradePointsStatistics>

    @Query("SELECT * FROM GradesPointsStatistics WHERE student_id = :studentId AND semester_id = :semesterId")
    fun loadAll(semesterId: Int, studentId: Int): Maybe<List<GradePointsStatistics>>
}
