package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.ui.base.BaseView

interface GradeView : BaseView {

    val currentPageIndex: Int

    fun initView()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showSemesterSwitch(show: Boolean)

    fun showSemesterDialog(selectedIndex: Int)

    fun enableSwipe(enable: Boolean)

    fun notifyChildLoadData(index: Int, semesterId: Int, forceRefresh: Boolean)

    fun notifyChildParentReselected(index: Int)

    fun notifyChildSemesterChange(index: Int)

    interface GradeChildView {

        fun onParentChangeSemester()

        fun onParentLoadData(semesterId: Int, forceRefresh: Boolean)

        fun onParentReselected()
    }
}
