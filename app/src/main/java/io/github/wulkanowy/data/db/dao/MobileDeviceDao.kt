package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.MobileDevice

@Dao
interface MobileDeviceDao : BaseDao<MobileDevice> {

    @Query("SELECT * FROM MobileDevices WHERE student_id = :studentId ORDER BY date DESC")
    suspend fun loadAll(studentId: Int): List<MobileDevice>
}
