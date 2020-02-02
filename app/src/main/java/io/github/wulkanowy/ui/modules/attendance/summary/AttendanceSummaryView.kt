package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.ui.base.BaseView

interface AttendanceSummaryView : BaseView {

    val totalString: String

    val isViewEmpty: Boolean

    fun initView()

    fun hideRefresh()

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun updateDataSet(data: List<AttendanceSummaryItem>, header: AttendanceSummaryScrollableHeader)

    fun updateSubjects(data: ArrayList<String>)

    fun showSubjects(show: Boolean)

    fun clearView()
}
