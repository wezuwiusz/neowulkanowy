package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseView

interface MessageTabView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun resetListPosition()

    fun updateData(data: List<MessageTabDataItem>)

    fun updateActionModeTitle(selectedMessagesSize: Int)

    fun updateSelectAllMenu(isAllSelected: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showMessagesDeleted()

    fun showErrorView(show: Boolean)

    fun notifyParentShowNewMessage(show: Boolean)

    fun setErrorDetails(message: String)

    fun showRefresh(show: Boolean)

    fun openMessage(message: Message)

    fun notifyParentDataLoaded()

    fun notifyParentShowActionMode(show: Boolean)

    fun hideKeyboard()

    fun showActionMode(show: Boolean)

    fun showRecyclerBottomPadding(show: Boolean)

    fun showMailboxChooser(mailboxes: List<Mailbox>)
}
