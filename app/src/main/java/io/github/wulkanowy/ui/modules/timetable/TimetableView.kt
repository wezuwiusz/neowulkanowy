package io.github.wulkanowy.ui.modules.timetable

import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface TimetableView : BaseView {

    val isViewEmpty: Boolean

    val currentStackSize: Int?

    fun initView()

    fun updateData(data: List<Timetable>, showWholeClassPlanType: String, showGroupsInPlanType: Boolean, showTimetableTimers: Boolean)

    fun updateNavigationDay(date: String)

    fun clearData()

    fun showRefresh(show: Boolean)

    fun resetView()

    fun showEmpty(show: Boolean)

    fun setDayHeaderMessage(message: String?)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showTimetableDialog(lesson: Timetable)

    fun showDatePickerDialog(currentDate: LocalDate)

    fun popView()

    fun openAdditionalLessonsView()

    fun openCompletedLessonsView()
}
