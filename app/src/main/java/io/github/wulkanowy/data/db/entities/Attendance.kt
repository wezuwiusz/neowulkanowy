package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Attendance")
data class Attendance(

        @ColumnInfo(name = "student_id")
        var studentId: Int,

        @ColumnInfo(name = "diary_id")
        var diaryId: Int,

        var date: LocalDate,

        var number: Int,

        var subject: String,

        var name: String,

        var presence: Boolean = false,

        var absence: Boolean = false,

        var exemption: Boolean = false,

        var lateness: Boolean = false,

        var excused: Boolean = false,

        var deleted: Boolean = false
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
