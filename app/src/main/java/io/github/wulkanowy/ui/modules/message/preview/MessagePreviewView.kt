package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface MessagePreviewView : BaseSessionView {

    val noSubjectString: String

    val deleteMessageSuccessString: String

    fun setSubject(subject: String)

    fun setRecipient(recipient: String)

    fun setSender(sender: String)

    fun setDate(date: String)

    fun setContent(content: String)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showOptions(show: Boolean)

    fun setDeletedOptionsLabels()

    fun setNotDeletedOptionsLabels()

    fun showMessageError()

    fun openMessageReply(message: Message?)

    fun openMessageForward(message: Message?)

    fun popView()

    fun notifyParentMessageDeleted(message: Message)
}
