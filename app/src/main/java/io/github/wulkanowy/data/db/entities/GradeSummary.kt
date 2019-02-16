package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Grades_Summary")
data class GradeSummary(

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val subject: String,

    val predictedGrade: String,

    val finalGrade: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
