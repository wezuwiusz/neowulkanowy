package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface MessagePreviewView : BaseSessionView {

    val noSubjectString: String

    fun setSubject(subject: String)

    fun setRecipient(recipient: String)

    fun setSender(sender: String)

    fun setDate(date: String)

    fun setContent(content: String)

    fun showProgress(show: Boolean)

    fun showOptions(show: Boolean)

    fun showMessageError()

    fun openMessageReply(message: Message?)

    fun openMessageForward(message: Message?)
}
