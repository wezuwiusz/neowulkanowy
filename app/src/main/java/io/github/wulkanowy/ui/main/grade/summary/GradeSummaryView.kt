package io.github.wulkanowy.ui.main.grade.summary

import io.github.wulkanowy.ui.base.BaseView

interface GradeSummaryView : BaseView {

    fun initView()

    fun updateDataSet(data: List<GradeSummaryItem>, header: GradeSummaryScrollableHeader)

    fun resetView()

    fun clearView()

    fun isViewEmpty(): Boolean

    fun showProgress(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun predictedString(): String

    fun finalString(): String

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()
}
