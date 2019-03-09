package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface CompletedLessonsDao {

    @Insert
    fun insertAll(exams: List<CompletedLesson>)

    @Delete
    fun deleteAll(exams: List<CompletedLesson>)

    @Query("SELECT * FROM CompletedLesson WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Maybe<List<CompletedLesson>>
}
