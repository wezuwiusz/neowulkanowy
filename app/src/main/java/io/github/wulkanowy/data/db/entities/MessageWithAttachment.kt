package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class MessageWithAttachment(
    @Embedded
    val message: Message,

    @Relation(parentColumn = "message_id", entityColumn = "message_id")
    val attachments: List<MessageAttachment>
)
