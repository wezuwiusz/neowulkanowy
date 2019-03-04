package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface GradeStatisticsView : BaseSessionView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateSubjects(data: ArrayList<String>)

    fun updateData(items: List<GradeStatistics>)

    fun showSubjects(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()

    fun clearView()

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showRefresh(show: Boolean)
}
