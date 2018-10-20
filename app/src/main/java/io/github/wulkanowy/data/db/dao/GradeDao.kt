package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.*
import io.github.wulkanowy.data.db.entities.Grade
import io.reactivex.Maybe

@Dao
interface GradeDao {

    @Insert
    fun insertAll(grades: List<Grade>)

    @Update
    fun update(grade: Grade)

    @Delete
    fun deleteAll(grades: List<Grade>)

    @Query("SELECT * FROM Grades WHERE semester_id = :semesterId AND student_id = :studentId")
    fun getGrades(semesterId: Int, studentId: Int): Maybe<List<Grade>>
}
