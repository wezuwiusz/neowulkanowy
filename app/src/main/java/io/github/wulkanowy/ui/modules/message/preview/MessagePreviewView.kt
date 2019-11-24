package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseView

interface MessagePreviewView : BaseView {

    val noSubjectString: String

    val deleteMessageSuccessString: String

    fun initView()

    fun setSubject(subject: String)

    fun setRecipient(recipient: String)

    fun setSender(sender: String)

    fun setDate(date: String)

    fun setContent(content: String)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun notifyParentMessageDeleted(message: Message)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun setErrorRetryCallback(callback: () -> Unit)

    fun showOptions(show: Boolean)

    fun setDeletedOptionsLabels()

    fun setNotDeletedOptionsLabels()

    fun openMessageReply(message: Message?)

    fun openMessageForward(message: Message?)

    fun popView()
}
