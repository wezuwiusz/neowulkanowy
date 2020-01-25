package io.github.wulkanowy.ui.modules.attendance

import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseView
import org.threeten.bp.LocalDate

interface AttendanceView : BaseView {

    val isViewEmpty: Boolean

    val currentStackSize: Int?

    val excuseSuccessString: String

    val excuseNoSelectionString: String

    val excuseActionMode: Boolean

    fun initView()

    fun updateData(data: List<AttendanceItem>)

    fun updateNavigationDay(date: String)

    fun clearData()

    fun hideRefresh()

    fun resetView()

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showExcuseButton(show: Boolean)

    fun showAttendanceDialog(lesson: Attendance)

    fun showDatePickerDialog(currentDate: LocalDate)

    fun showExcuseDialog()

    fun openSummaryView()

    fun startActionMode()

    fun showExcuseCheckboxes(show: Boolean)

    fun finishActionMode()

    fun popView()
}
