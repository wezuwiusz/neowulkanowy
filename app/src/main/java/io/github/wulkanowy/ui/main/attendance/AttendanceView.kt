package io.github.wulkanowy.ui.main.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseView

interface AttendanceView : BaseView {

    fun initView()

    fun updateData(data: List<AttendanceItem>)

    fun clearData()

    fun updateNavigationDay(date: String)

    fun isViewEmpty(): Boolean

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showAttendanceDialog(lesson: Attendance)
}
