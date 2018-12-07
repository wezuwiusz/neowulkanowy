package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.ui.base.BaseView

interface AttendanceSummaryView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun hideRefresh()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showEmpty(show: Boolean)

    fun updateDataSet(data: List<AttendanceSummaryItem>, header: AttendanceSummaryScrollableHeader)

    fun updateSubjects(data: ArrayList<String>)

    fun showSubjects(show: Boolean)

    fun clearView()
}
