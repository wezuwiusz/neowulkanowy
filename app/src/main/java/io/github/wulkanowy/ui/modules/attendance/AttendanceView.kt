package io.github.wulkanowy.ui.modules.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseView

interface AttendanceView : BaseView {

    val isViewEmpty: Boolean

    val currentStackSize: Int?

    fun initView()

    fun updateData(data: List<AttendanceItem>)

    fun updateNavigationDay(date: String)

    fun clearData()

    fun hideRefresh()

    fun resetView()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showAttendanceDialog(lesson: Attendance)

    fun openSummaryView()

    fun popView()
}
