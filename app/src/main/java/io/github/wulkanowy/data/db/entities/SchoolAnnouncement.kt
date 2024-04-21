package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "SchoolAnnouncements")
data class SchoolAnnouncement(

    @ColumnInfo(name = "user_login_id") // todo: change column name
    val studentId: Int,

    val date: LocalDate,

    val subject: String,

    val content: String,

    val author: String? = null,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
