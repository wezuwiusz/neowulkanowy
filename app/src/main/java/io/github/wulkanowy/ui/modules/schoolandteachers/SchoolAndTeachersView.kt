package io.github.wulkanowy.ui.modules.schoolandteachers

import io.github.wulkanowy.ui.base.BaseView

interface SchoolAndTeachersView : BaseView {

    val currentPageIndex: Int

    fun initView()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun notifyChildLoadData(index: Int, forceRefresh: Boolean)
}
