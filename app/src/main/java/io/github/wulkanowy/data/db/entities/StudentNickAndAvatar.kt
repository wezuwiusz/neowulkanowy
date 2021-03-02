package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class StudentNickAndAvatar(

    val nick: String,

    @ColumnInfo(name = "avatar_color")
    var avatarColor: Long

) : Serializable {

    @PrimaryKey
    var id: Long = 0
}
