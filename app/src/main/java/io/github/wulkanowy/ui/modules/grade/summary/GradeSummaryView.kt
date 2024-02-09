package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.ui.base.BaseView

interface GradeSummaryView : BaseView {

    val isViewEmpty: Boolean

    val predictedString: String

    val finalString: String

    fun initView()

    fun updateData(data: List<GradeSummaryItem>)

    fun resetView()

    fun clearView()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showEmpty(show: Boolean)

    fun showCalculatedAverageHelpDialog()

    fun showFinalAverageHelpDialog()

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()
}
