package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "Notes")
data class Note(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val date: LocalDate,

    val teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    val teacherSymbol: String,

    val category: String,

    @ColumnInfo(name = "category_type")
    val categoryType: Int,

    @ColumnInfo(name = "is_points_show")
    val isPointsShow: Boolean,

    val points: Int,

    val content: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_read")
    var isRead: Boolean = true

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
