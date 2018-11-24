package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe

@Dao
interface SemesterDao {

    @Insert(onConflict = IGNORE)
    fun insertAll(semester: List<Semester>)

    @Query("UPDATE Semesters SET is_current = 1 WHERE semester_id = :semesterId AND diary_id = :diaryId")
    fun update(semesterId: Int, diaryId: Int)

    @Query("SELECT * FROM Semesters WHERE student_id = :studentId")
    fun load(studentId: Int): Maybe<List<Semester>>

    @Query("UPDATE Semesters SET is_current = 0 WHERE student_id = :studentId")
    fun resetCurrent(studentId: Int)
}
