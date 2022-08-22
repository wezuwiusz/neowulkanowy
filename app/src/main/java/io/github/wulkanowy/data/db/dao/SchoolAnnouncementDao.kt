package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
@Singleton
interface SchoolAnnouncementDao : BaseDao<SchoolAnnouncement> {

    @Query("SELECT * FROM SchoolAnnouncements WHERE user_login_id = :userLoginId ORDER BY date DESC")
    fun loadAll(userLoginId: Int): Flow<List<SchoolAnnouncement>>
}
