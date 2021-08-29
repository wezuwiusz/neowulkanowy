package io.github.wulkanowy.data.pojos

import com.squareup.moshi.JsonClass
import io.github.wulkanowy.ui.modules.message.send.RecipientChipItem

@JsonClass(generateAdapter = true)
data class MessageDraft(
    val recipients: List<RecipientChipItem>,
    val subject: String,
    val content: String,
)