package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "LuckyNumbers")
data class LuckyNumber (

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val date: LocalDate,

    @ColumnInfo(name = "lucky_number")
    val luckyNumber: Int

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
