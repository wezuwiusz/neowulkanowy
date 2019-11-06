package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Timetable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface TimetableDao : BaseDao<Timetable> {

    @Query("SELECT * FROM Timetable WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Maybe<List<Timetable>>
}
