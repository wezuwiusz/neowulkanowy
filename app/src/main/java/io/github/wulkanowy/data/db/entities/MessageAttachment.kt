package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity(
    tableName = "MessageAttachments",
    primaryKeys = ["message_global_key", "url", "filename"],
)
data class MessageAttachment(

    @ColumnInfo(name = "message_global_key")
    val messageGlobalKey: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "filename")
    val filename: String
) : Serializable
