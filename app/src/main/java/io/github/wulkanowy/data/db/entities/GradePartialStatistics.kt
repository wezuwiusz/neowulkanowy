package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GradePartialStatistics")
data class GradePartialStatistics(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    val subject: String,

    @ColumnInfo(name = "class_average")
    val classAverage: String,

    @ColumnInfo(name = "student_average")
    val studentAverage: String,

    @ColumnInfo(name = "class_amounts")
    val classAmounts: List<Int>,

    @ColumnInfo(name = "student_amounts")
    val studentAmounts: List<Int>

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
