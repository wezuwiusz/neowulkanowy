package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GradesSummary")
data class GradeSummary(

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val position: Int,

    val subject: String,

    @ColumnInfo(name = "predicted_grade")
    val predictedGrade: String,

    @ColumnInfo(name = "final_grade")
    val finalGrade: String,

    @ColumnInfo(name = "proposed_points")
    val proposedPoints: String,

    @ColumnInfo(name = "final_points")
    val finalPoints: String,

    @ColumnInfo(name = "points_sum")
    val pointsSum: String,

    val average: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
