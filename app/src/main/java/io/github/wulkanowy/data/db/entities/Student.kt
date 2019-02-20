package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity(tableName = "Students", indices = [Index(value = ["email", "symbol", "student_id", "school_id"], unique = true)])
data class Student(

    val endpoint: String,

    val loginType: String,

    val email: String,

    var password: String,

    val symbol: String,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "student_name")
    val studentName: String,

    @ColumnInfo(name = "school_id")
    val schoolSymbol: String,

    @ColumnInfo(name = "school_name")
    val schoolName: String,

    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean,

    @ColumnInfo(name = "registration_date")
    val registrationDate: LocalDateTime
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
