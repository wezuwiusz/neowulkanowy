package io.github.wulkanowy.ui.modules.message

import io.github.wulkanowy.ui.base.BaseView

interface MessageView : BaseView {

    val currentPageIndex: Int

    fun initView()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showNewMessage(show: Boolean)

    fun showTabLayout(show: Boolean)

    fun notifyChildLoadData(index: Int, forceRefresh: Boolean)

    fun notifyChildrenFinishActionMode()

    fun notifyChildParentReselected(index: Int)

    fun openSendMessage()

    fun popView()
}
