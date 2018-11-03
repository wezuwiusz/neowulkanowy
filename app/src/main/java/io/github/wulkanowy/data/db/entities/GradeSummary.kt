package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Grades_Summary")
data class GradeSummary(

        @ColumnInfo(name = "semester_id")
        var semesterId: Int,

        @ColumnInfo(name = "student_id")
        var studentId: Int,

        var subject: String,

        var predictedGrade: String,

        var finalGrade: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}
