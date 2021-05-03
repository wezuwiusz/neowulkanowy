package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface SchoolAnnouncementDao : BaseDao<SchoolAnnouncement> {

    @Query("SELECT * FROM SchoolAnnouncements WHERE student_id = :studentId")
    fun loadAll(studentId: Int): Flow<List<SchoolAnnouncement>>
}
