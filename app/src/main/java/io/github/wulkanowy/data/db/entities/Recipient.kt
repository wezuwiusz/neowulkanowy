package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "Recipients")
data class Recipient(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "real_id")
    val realId: String,

    val name: String,

    @ColumnInfo(name = "real_name")
    val realName: String,

    @ColumnInfo(name = "login_id")
    val loginId: Int,

    @ColumnInfo(name = "unit_id")
    val unitId: Int,

    val role: Int,

    val hash: String

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString() = name
}
