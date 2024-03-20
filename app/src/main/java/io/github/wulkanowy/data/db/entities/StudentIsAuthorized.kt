package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class StudentIsAuthorized(
    @ColumnInfo(name = "is_authorized", defaultValue = "0")
    val isAuthorized: Boolean,
) : Serializable {

    @PrimaryKey
    var id: Long = 0
}
