package io.github.wulkanowy.ui.modules.message.mailboxchooser

import io.github.wulkanowy.data.db.entities.Mailbox

data class MailboxChooserItem(
    val mailbox: Mailbox? = null,
    val isAll: Boolean = false,
    val onClickListener: (Mailbox?) -> Unit,
)
