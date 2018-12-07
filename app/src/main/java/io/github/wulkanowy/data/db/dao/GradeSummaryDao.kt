package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface GradeSummaryDao {

    @Insert
    fun insertAll(gradesSummary: List<GradeSummary>)

    @Delete
    fun deleteAll(gradesSummary: List<GradeSummary>)

    @Query("SELECT * FROM grades_summary WHERE student_id = :studentId AND semester_id = :semesterId")
    fun loadAll(semesterId: Int, studentId: Int): Maybe<List<GradeSummary>>
}
