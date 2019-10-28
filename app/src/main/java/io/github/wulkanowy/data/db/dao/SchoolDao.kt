package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.School
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface SchoolDao {

    @Insert
    fun insert(school: School)

    @Delete
    fun delete(school: School)

    @Query("SELECT * FROM School WHERE student_id = :studentId AND class_id = :classId")
    fun load(studentId: Int, classId: Int): Maybe<School>
}
