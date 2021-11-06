package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Attendance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface AttendanceDao : BaseDao<Attendance> {

    @Query("SELECT * FROM Attendance WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :start AND date <= :end")
    fun loadAll(
        diaryId: Int,
        studentId: Int,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<Attendance>>
}
