package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GradeSemesterStatistics")
data class GradeSemesterStatistics(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    val subject: String,

    val amounts: List<Int>,

    @ColumnInfo(name = "student_grade")
    val studentGrade: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Transient
    var classAverage: String = ""

    @Transient
    var studentAverage: String = ""
}
