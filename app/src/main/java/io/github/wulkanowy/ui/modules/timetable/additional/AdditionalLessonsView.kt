package io.github.wulkanowy.ui.modules.timetable.additional

import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface AdditionalLessonsView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<TimetableAdditional>)

    fun clearData()

    fun updateNavigationDay(date: String)

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showDatePickerDialog(currentDate: LocalDate)
}
