package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "Messages")
data class Message(

    @ColumnInfo(name = "message_global_key")
    val messageGlobalKey: String,

    @ColumnInfo(name = "mailbox_key")
    val mailboxKey: String,

    @ColumnInfo(name = "message_id")
    val messageId: Int,

    val correspondents: String,

    val subject: String,

    val date: Instant,

    @ColumnInfo(name = "folder_id")
    val folderId: Int,

    var unread: Boolean,

    @ColumnInfo(name = "has_attachments")
    val hasAttachments: Boolean
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true

    var content: String = ""
    var sender: String? = null
    var recipients: String? = null
}
