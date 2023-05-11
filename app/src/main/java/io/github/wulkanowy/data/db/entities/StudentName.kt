package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class StudentName(

    @ColumnInfo(name = "student_name")
    val studentName: String

) : Serializable {

    @PrimaryKey
    var id: Long = 0
}
