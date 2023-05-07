package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface TimetableWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<StudentWithSemesters>, selectedStudentId: Long)

    fun updateTimetableWidget(widgetId: Int)

    fun setSuccessResult(widgetId: Int)

    fun finishView()

    fun openLoginView()
}
