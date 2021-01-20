package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ReportingUnits")
data class ReportingUnit(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "real_id")
    val unitId: Int,

    @ColumnInfo(name = "short")
    val shortName: String,

    @ColumnInfo(name = "sender_id")
    val senderId: Int,

    @ColumnInfo(name = "sender_name")
    val senderName: String,

    val roles: List<Int>

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
