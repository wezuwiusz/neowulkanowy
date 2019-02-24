package io.github.wulkanowy.ui.modules.message

import io.github.wulkanowy.ui.base.BaseView

interface MessageView : BaseView {

    val currentPageIndex: Int

    fun initView()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun notifyChildLoadData(index: Int, forceRefresh: Boolean)

    fun openSendMessage()

    interface MessageChildView {

        fun onParentLoadData(forceRefresh: Boolean)
    }
}
