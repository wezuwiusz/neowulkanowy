package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Teacher
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface TeacherDao {

    @Insert
    fun insertAll(devices: List<Teacher>)

    @Delete
    fun deleteAll(devices: List<Teacher>)

    @Query("SELECT * FROM Teachers WHERE student_id = :studentId AND class_id = :classId")
    fun loadAll(studentId: Int, classId: Int): Maybe<List<Teacher>>
}
