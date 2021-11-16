package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "Attendance")
data class Attendance(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    @ColumnInfo(name = "time_id")
    val timeId: Int,

    val date: LocalDate,

    val number: Int,

    val subject: String,

    val name: String,

    val presence: Boolean,

    val absence: Boolean,

    val exemption: Boolean,

    val lateness: Boolean,

    val excused: Boolean,

    val deleted: Boolean,

    val excusable: Boolean,

    @ColumnInfo(name = "excuse_status")
    val excuseStatus: String?

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
