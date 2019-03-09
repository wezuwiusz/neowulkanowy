package io.github.wulkanowy.ui.modules.message.tab

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.session.BaseSessionView
import io.github.wulkanowy.ui.modules.message.MessageItem

interface MessageTabView : BaseSessionView {

    val noSubjectString: String

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MessageItem>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun clearView()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showRefresh(show: Boolean)

    fun openMessage(messageId: Int?)

    fun notifyParentDataLoaded()
}
