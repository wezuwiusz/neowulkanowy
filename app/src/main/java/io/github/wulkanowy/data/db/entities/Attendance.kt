package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Attendance")
data class Attendance(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val date: LocalDate,

    val number: Int,

    val subject: String,

    val name: String,

    val presence: Boolean,

    val absence: Boolean,

    val exemption: Boolean,

    val lateness: Boolean,

    val excused: Boolean,

    val deleted: Boolean
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
