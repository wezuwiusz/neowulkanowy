package io.github.wulkanowy.ui.modules.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface HomeworkView : BaseSessionView {

    fun initView()

    fun updateData(data: List<HomeworkItem>)

    fun clearData()

    fun updateNavigationDay(date: String)

    fun isViewEmpty(): Boolean

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showTimetableDialog(homework: Homework)
}
