package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface SemesterDao {

    @Insert
    fun insertAll(semester: List<Semester>)

    @Delete
    fun deleteAll(semester: List<Semester>)

    @Query("SELECT * FROM Semesters WHERE student_id = :studentId")
    fun loadAll(studentId: Int): Maybe<List<Semester>>
}
