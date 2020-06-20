package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.AttendanceSummary

@Dao
interface AttendanceSummaryDao : BaseDao<AttendanceSummary> {

    @Query("SELECT * FROM AttendanceSummary WHERE diary_id = :diaryId AND student_id = :studentId AND subject_id = :subjectId")
    suspend fun loadAll(diaryId: Int, studentId: Int, subjectId: Int): List<AttendanceSummary>
}
