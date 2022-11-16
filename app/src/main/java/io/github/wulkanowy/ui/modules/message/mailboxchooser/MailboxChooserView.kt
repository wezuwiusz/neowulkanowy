package io.github.wulkanowy.ui.modules.message.mailboxchooser

import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.ui.base.BaseView

interface MailboxChooserView : BaseView {

    fun initView()

    fun submitData(items: List<MailboxChooserItem>)

    fun onMailboxSelected(item: Mailbox?)
}
