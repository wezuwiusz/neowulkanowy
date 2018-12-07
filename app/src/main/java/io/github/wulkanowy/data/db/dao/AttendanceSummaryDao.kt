package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.reactivex.Maybe

@Dao
interface AttendanceSummaryDao {

    @Insert
    fun insertAll(exams: List<AttendanceSummary>): List<Long>

    @Delete
    fun deleteAll(exams: List<AttendanceSummary>)

    @Query("SELECT * FROM AttendanceSummary WHERE diary_id = :diaryId AND student_id = :studentId AND subject_id = :subjectId")
    fun loadAll(diaryId: Int, studentId: Int, subjectId: Int): Maybe<List<AttendanceSummary>>
}
