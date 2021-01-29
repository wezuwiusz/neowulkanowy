package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.StudentInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
@Dao
interface StudentInfoDao : BaseDao<StudentInfo> {

    @Query("SELECT * FROM StudentInfo WHERE student_id = :studentId")
    fun loadStudentInfo(studentId: Int): Flow<StudentInfo?>
}
