package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.BaseView

interface GradeStatisticsView : BaseView {

    val isPieViewEmpty: Boolean

    val isBarViewEmpty: Boolean

    fun initView()

    fun updateSubjects(data: ArrayList<String>)

    fun updatePieData(items: List<GradeStatistics>, theme: String)

    fun updateBarData(item: GradePointsStatistics)

    fun showSubjects(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()

    fun clearView()

    fun showPieContent(show: Boolean)

    fun showBarContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showRefresh(show: Boolean)
}
