package io.github.wulkanowy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "Recipients")
data class Recipient(
    val mailboxGlobalKey: String,
    val studentMailboxGlobalKey: String,
    val fullName: String,
    val userName: String,
    val schoolShortName: String,
    val type: MailboxType,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString() = userName
}
