package io.github.wulkanowy.ui.modules.message.send

import com.squareup.moshi.JsonClass
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.materialchipsinput.ChipItem

@JsonClass(generateAdapter = true)
data class RecipientChipItem(

    override val title: String,

    override val summary: String,

    val recipient: Recipient

) : ChipItem
