package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "MobileDevices")
data class MobileDevice(

    @ColumnInfo(name = "student_id")
    val userLoginId: Int,

    @ColumnInfo(name = "device_id")
    val deviceId: Int,

    val name: String,

    val date: LocalDateTime
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
