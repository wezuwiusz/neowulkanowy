package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.TimetableHeader
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Dao
@Singleton
interface TimetableHeaderDao : BaseDao<TimetableHeader> {

    @Query("SELECT * FROM TimetableHeaders WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Flow<List<TimetableHeader>>
}
