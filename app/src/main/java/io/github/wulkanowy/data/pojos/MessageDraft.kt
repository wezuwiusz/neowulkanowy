package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.ui.modules.message.send.RecipientChipItem
import kotlinx.serialization.Serializable

@Serializable
data class MessageDraft(
    val recipients: List<RecipientChipItem>,
    val subject: String,
    val content: String,
)