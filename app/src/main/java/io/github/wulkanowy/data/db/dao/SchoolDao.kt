package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.School
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface SchoolDao : BaseDao<School> {

    @Query("SELECT * FROM School WHERE student_id = :studentId AND class_id = :classId")
    fun load(studentId: Int, classId: Int): Flow<School?>
}
