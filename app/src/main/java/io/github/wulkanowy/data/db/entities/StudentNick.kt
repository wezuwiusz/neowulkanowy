package io.github.wulkanowy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class StudentNick(

    val nick: String

) : Serializable {

    @PrimaryKey
    var id: Long = 0
}
