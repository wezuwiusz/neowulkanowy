package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.ui.base.BaseView

interface GradeSummaryView : BaseView {

    val isViewEmpty: Boolean

    val predictedString: String

    val finalString: String

    fun initView()

    fun updateData(data: List<GradeSummaryItem>, header: GradeSummaryScrollableHeader)

    fun resetView()

    fun clearView()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()
}
