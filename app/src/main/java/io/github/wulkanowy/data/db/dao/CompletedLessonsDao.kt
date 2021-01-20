package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.CompletedLesson
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface CompletedLessonsDao : BaseDao<CompletedLesson> {

    @Query("SELECT * FROM CompletedLesson WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(studentId: Int, diaryId: Int, from: LocalDate, end: LocalDate): Flow<List<CompletedLesson>>
}
