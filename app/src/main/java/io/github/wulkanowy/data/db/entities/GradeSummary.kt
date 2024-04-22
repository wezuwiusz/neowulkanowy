package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

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

    @ColumnInfo(name = "points_sum_all_year")
    val pointsSumAllYear: String?,

    val average: Double,

    @ColumnInfo(name = "average_all_year")
    val averageAllYear: Double? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_predicted_grade_notified")
    var isPredictedGradeNotified: Boolean = true

    @ColumnInfo(name = "is_final_grade_notified")
    var isFinalGradeNotified: Boolean = true

    @ColumnInfo(name = "predicted_grade_last_change")
    var predictedGradeLastChange: Instant = Instant.now()

    @ColumnInfo(name = "final_grade_last_change")
    var finalGradeLastChange: Instant = Instant.now()
}
