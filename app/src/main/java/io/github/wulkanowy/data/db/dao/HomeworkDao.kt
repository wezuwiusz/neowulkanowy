package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Homework
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface HomeworkDao {

    @Insert
    fun insertAll(homework: List<Homework>)

    @Delete
    fun deleteAll(homework: List<Homework>)

    @Query("SELECT * FROM Homework WHERE semester_id = :semesterId AND student_id = :studentId AND date >= :from AND date <= :end")
    fun loadAll(semesterId: Int, studentId: Int, from: LocalDate, end: LocalDate): Maybe<List<Homework>>
}
