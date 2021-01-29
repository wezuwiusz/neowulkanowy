package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity(
    tableName = "Students",
    indices = [Index(
        value = ["email", "symbol", "student_id", "school_id", "class_id"],
        unique = true
    )]
)
data class Student(

    @ColumnInfo(name = "scrapper_base_url")
    val scrapperBaseUrl: String,

    @ColumnInfo(name = "mobile_base_url")
    val mobileBaseUrl: String,

    @ColumnInfo(name = "login_type")
    val loginType: String,

    @ColumnInfo(name = "login_mode")
    val loginMode: String,

    @ColumnInfo(name = "certificate_key")
    val certificateKey: String,

    @ColumnInfo(name = "private_key")
    val privateKey: String,

    @ColumnInfo(name = "is_parent")
    val isParent: Boolean,

    val email: String,

    var password: String,

    val symbol: String,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "user_login_id")
    val userLoginId: Int,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "student_name")
    val studentName: String,

    @ColumnInfo(name = "school_id")
    val schoolSymbol: String,

    @ColumnInfo(name = "school_short")
    val schoolShortName: String,

    @ColumnInfo(name = "school_name")
    val schoolName: String,

    @ColumnInfo(name = "class_name")
    val className: String,

    @ColumnInfo(name = "class_id")
    val classId: Int,

    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean,

    @ColumnInfo(name = "registration_date")
    val registrationDate: LocalDateTime
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
