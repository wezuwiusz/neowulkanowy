package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceSummaryDao : BaseDao<AttendanceSummary> {

    @Query("SELECT * FROM AttendanceSummary WHERE diary_id = :diaryId AND student_id = :studentId AND subject_id = :subjectId")
    fun loadAll(diaryId: Int, studentId: Int, subjectId: Int): Flow<List<AttendanceSummary>>
}
