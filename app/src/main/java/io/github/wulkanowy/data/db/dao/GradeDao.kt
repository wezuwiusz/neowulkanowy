package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.Grade
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface GradeDao {

    @Insert
    fun insertAll(grades: List<Grade>)

    @Update
    fun update(grade: Grade)

    @Update
    fun updateAll(grade: List<Grade>)

    @Delete
    fun deleteAll(grades: List<Grade>)

    @Query("SELECT * FROM Grades WHERE semester_id = :semesterId AND student_id = :studentId")
    fun load(semesterId: Int, studentId: Int): Maybe<List<Grade>>

    @Query("SELECT * FROM Grades WHERE is_read = 0 AND semester_id = :semesterId AND student_id = :studentId")
    fun loadNew(semesterId: Int, studentId: Int): Maybe<List<Grade>>
}
