package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.ui.base.BaseView

interface TimetableWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<TimetableWidgetConfigureItem>)

    fun updateTimetableWidget(widgetId: Int)

    fun showThemeDialog()

    fun setSuccessResult(widgetId: Int)

    fun finishView()

    fun openLoginView()
}
