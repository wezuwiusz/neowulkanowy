package io.github.wulkanowy.ui.modules.luckynumberwidget

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetConfigureItem

interface LuckyNumberWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<LuckyNumberWidgetConfigureItem>)

    fun updateLuckyNumberWidget(widgetId: Int)

    fun setSuccessResult(widgetId: Int)

    fun finishView()

    fun openLoginView()
}
