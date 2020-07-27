package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "Grades")
data class Grade(

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val subject: String,

    val entry: String,

    val value: Double,

    val modifier: Double,

    val comment: String,

    val color: String,

    @ColumnInfo(name = "grade_symbol")
    val gradeSymbol: String,

    val description: String,

    val weight: String,

    val weightValue: Double,

    val date: LocalDate,

    val teacher: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_read")
    var isRead: Boolean = true

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
