package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Subjects")
data class Subject(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    @ColumnInfo(name = "real_id")
    val realId: Int,

    val name: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
