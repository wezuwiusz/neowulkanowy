package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface GradeStatisticsDao {

    @Insert
    fun insertAll(gradesStatistics: List<GradeStatistics>)

    @Delete
    fun deleteAll(gradesStatistics: List<GradeStatistics>)

    @Query("SELECT * FROM GradesStatistics WHERE student_id = :studentId AND semester_id = :semesterId AND subject = :subjectName AND is_semester = :isSemester")
    fun loadSubject(semesterId: Int, studentId: Int, subjectName: String, isSemester: Boolean): Maybe<List<GradeStatistics>>

    @Query("SELECT * FROM GradesStatistics WHERE student_id = :studentId AND semester_id = :semesterId AND is_semester = :isSemester")
    fun loadAll(semesterId: Int, studentId: Int, isSemester: Boolean): Maybe<List<GradeStatistics>>
}
