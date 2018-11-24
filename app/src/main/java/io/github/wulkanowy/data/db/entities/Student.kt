package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Students", indices = [Index(value = ["email", "symbol", "student_id", "school_id"], unique = true)])
data class Student(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var endpoint: String,

    var loginType: String,

    var email: String,

    var password: String,

    var symbol: String = "",

    @ColumnInfo(name = "student_id")
    var studentId: Int = 0,

    @ColumnInfo(name = "student_name")
    var studentName: String = "",

    @ColumnInfo(name = "school_id")
    var schoolSymbol: String = "",

    @ColumnInfo(name = "school_name")
    var schoolName: String = "",

    @ColumnInfo(name = "is_current")
    var isCurrent: Boolean = false
)
