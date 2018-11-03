package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Grades")
data class Grade(

    @ColumnInfo(name = "semester_id")
    var semesterId: Int,

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    var subject: String,

    var entry: String,

    var value: Int,

    var modifier: Double,

    var comment: String,

    var color: String,

    @ColumnInfo(name = "grade_symbol")
    var gradeSymbol: String,

    var description: String,

    var weight: String,

    var weightValue: Int,

    var date: LocalDate,

    var teacher: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_read")
    var isRead: Boolean = true

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
