package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.materialchipsinput.ChipItem

data class RecipientChipItem(

    override val title: String,

    override val summary: String,

    val recipient: Recipient

) : ChipItem
