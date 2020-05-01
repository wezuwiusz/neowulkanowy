package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface TimetableWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<Pair<Student, Boolean>>)

    fun updateTimetableWidget(widgetId: Int)

    fun showThemeDialog()

    fun setSuccessResult(widgetId: Int)

    fun finishView()

    fun openLoginView()
}
