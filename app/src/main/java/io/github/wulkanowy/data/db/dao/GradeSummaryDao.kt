package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.reactivex.Maybe

@Dao
interface GradeSummaryDao {

    @Insert(onConflict = REPLACE)
    fun insertAll(gradesSummary: List<GradeSummary>)

    @Query("SELECT * FROM grades_summary WHERE student_id = :studentId AND semester_id = :semesterId")
    fun getGradesSummary(semesterId: String, studentId: String): Maybe<List<GradeSummary>>
}
