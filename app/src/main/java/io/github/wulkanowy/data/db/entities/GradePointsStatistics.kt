package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GradesPointsStatistics")
data class GradePointsStatistics(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    val subject: String,

    val others: Double,

    val student: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
