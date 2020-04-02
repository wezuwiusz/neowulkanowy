package io.github.wulkanowy.ui.modules.message.tab

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.message.MessageItem

interface MessageTabView : BaseView {

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

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showRefresh(show: Boolean)

    fun openMessage(message: Message)

    fun notifyParentDataLoaded()
}
