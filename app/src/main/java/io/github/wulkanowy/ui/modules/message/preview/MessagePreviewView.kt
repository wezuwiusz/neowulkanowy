package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.ui.base.session.BaseSessionView

interface MessagePreviewView : BaseSessionView {

    val noSubjectString: String

    fun setSubject(subject: String)

    fun setRecipient(recipient: String?)

    fun setSender(sender: String?)

    fun setDate(date: String?)

    fun setContent(content: String?)

    fun showProgress(show: Boolean)

    fun showMessageError()
}
