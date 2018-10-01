package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.wulkanowy.data.db.entities.Attendance
import io.reactivex.Maybe
import org.threeten.bp.LocalDate

@Dao
interface AttendanceDao {

    @Insert
    fun insertAll(exams: List<Attendance>): List<Long>

    @Delete
    fun deleteAll(exams: List<Attendance>)

    @Query("SELECT * FROM Attendance WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun getExams(diaryId: String, studentId: String, from: LocalDate, end: LocalDate): Maybe<List<Attendance>>
}
