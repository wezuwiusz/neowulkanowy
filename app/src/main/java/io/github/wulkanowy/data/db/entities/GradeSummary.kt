package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Grades_Summary",
        indices = [Index(value = ["semester_id", "student_id", "subject"], unique = true)])
data class GradeSummary(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "semester_id")
        var semesterId: String,

        @ColumnInfo(name = "student_id")
        var studentId: String,

        var subject: String,

        var predictedGrade: String,

        var finalGrade: String
)
