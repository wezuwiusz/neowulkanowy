package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.ui.base.BaseView

interface AttendanceSummaryView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun updateDataSet(data: List<AttendanceSummary>)

    fun updateSubjects(data: ArrayList<String>)

    fun showSubjects(show: Boolean)

    fun clearView()
}
