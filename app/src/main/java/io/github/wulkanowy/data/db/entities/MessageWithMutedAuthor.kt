package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class MessageWithMutedAuthor(
    @Embedded
    val message: Message,

    @Relation(parentColumn = "correspondents", entityColumn = "author")
    val mutedMessageSender: MutedMessageSender?,
)
