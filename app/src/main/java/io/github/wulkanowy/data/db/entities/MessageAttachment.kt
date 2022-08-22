package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "MessageAttachments")
data class MessageAttachment(

    @PrimaryKey
    @ColumnInfo(name = "real_id")
    val realId: Int,

    @ColumnInfo(name = "message_global_key")
    val messageGlobalKey: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "filename")
    val filename: String
) : Serializable
