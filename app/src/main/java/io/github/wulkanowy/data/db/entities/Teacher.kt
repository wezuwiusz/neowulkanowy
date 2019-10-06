package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Teachers")
data class Teacher(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "class_id")
    val classId: Int,

    val subject: String,

    val name: String,

    @ColumnInfo(name = "short_name")
    val shortName: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
