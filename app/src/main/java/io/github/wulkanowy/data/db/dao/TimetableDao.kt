package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Timetable
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface TimetableDao : BaseDao<Timetable> {

    @Query("SELECT * FROM Timetable WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Flow<List<Timetable>>

    @Query("SELECT * FROM Timetable WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun load(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): List<Timetable>
}
