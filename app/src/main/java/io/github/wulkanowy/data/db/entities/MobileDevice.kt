package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "MobileDevices")
data class MobileDevice(

    @ColumnInfo(name = "user_login_id")
    val userLoginId: Int,

    @ColumnInfo(name = "device_id")
    val deviceId: Int,

    val name: String,

    val date: Instant,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
