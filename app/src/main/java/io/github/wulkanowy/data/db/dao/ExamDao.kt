package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.wulkanowy.data.db.entities.Exam
import io.reactivex.Maybe
import java.util.*

@Dao
interface ExamDao {

    @Insert
    fun insertAll(exams: List<Exam>): List<Long>

    @Delete
    fun deleteAll(exams: List<Exam>)

    @Query("SELECT * FROM Exams WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun getExams(diaryId: String, studentId: String, from: Date, end: Date): Maybe<List<Exam>>
}
