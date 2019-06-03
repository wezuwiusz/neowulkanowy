package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.reactivex.Maybe

@Dao
interface MobileDeviceDao {

    @Insert
    fun insertAll(devices: List<MobileDevice>)

    @Delete
    fun deleteAll(devices: List<MobileDevice>)

    @Query("SELECT * FROM MobileDevices WHERE student_id = :studentId ORDER BY date DESC")
    fun loadAll(studentId: Int): Maybe<List<MobileDevice>>
}
