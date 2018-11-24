package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Timetable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate

@Dao
interface TimetableDao {

    @Insert
    fun insertAll(exams: List<Timetable>): List<Long>

    @Delete
    fun deleteAll(exams: List<Timetable>)

    @Query("SELECT * FROM Timetable WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun load(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Maybe<List<Timetable>>
}
