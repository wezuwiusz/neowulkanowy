package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Exam
import io.reactivex.Maybe
import org.threeten.bp.LocalDate

@Dao
interface ExamDao {

    @Insert
    fun insertAll(exams: List<Exam>): List<Long>

    @Delete
    fun deleteAll(exams: List<Exam>)

    @Query("SELECT * FROM Exams WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun getExams(diaryId: Int, studentId: Int, from: LocalDate, end: LocalDate): Maybe<List<Exam>>
}
