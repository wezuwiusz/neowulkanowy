package io.github.wulkanowy.ui.main.grade

import io.github.wulkanowy.ui.base.BaseView

interface GradeView : BaseView {

    fun initView()

    fun currentPageIndex(): Int

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showSemesterDialog(selectedIndex: Int)

    fun notifyChildLoadData(index: Int, semesterId: String, forceRefresh: Boolean)

    fun notifyChildParentReselected(index: Int)

    fun notifyChildSemesterChange(index: Int)

    interface GradeChildView {

        fun onParentChangeSemester()

        fun onParentLoadData(semesterId: String, forceRefresh: Boolean)

        fun onParentReselected()
    }
}
