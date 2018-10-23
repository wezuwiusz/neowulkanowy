package io.github.wulkanowy.ui.modules.timetable

import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.ui.base.BaseView

interface TimetableView : BaseView {

    val roomString: String

    fun initView()

    fun updateData(data: List<TimetableItem>)

    fun updateNavigationDay(date: String)

    fun isViewEmpty(): Boolean

    fun clearData()

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showTimetableDialog(lesson: Timetable)
}
