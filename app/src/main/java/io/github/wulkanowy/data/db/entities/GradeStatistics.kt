package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GradesStatistics")
data class GradeStatistics(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    val subject: String,

    val grade: Int,

    val amount: Int,

    @ColumnInfo(name = "is_semester")
    val semester: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
