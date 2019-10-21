package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "School")
data class School(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "class_id")
    val classId: Int,

    val name: String,

    val address: String,

    val contact: String,

    val headmaster: String,

    val pedagogue: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
