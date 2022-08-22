package io.github.wulkanowy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Mailboxes")
data class Mailbox(

    @PrimaryKey
    val globalKey: String,
    val fullName: String,
    val userName: String,
    val userLoginId: Int,
    val studentName: String,
    val schoolNameShort: String,
    val type: MailboxType,
)

enum class MailboxType {
    STUDENT,
    PARENT,
    GUARDIAN,
    EMPLOYEE,
    UNKNOWN,
}
