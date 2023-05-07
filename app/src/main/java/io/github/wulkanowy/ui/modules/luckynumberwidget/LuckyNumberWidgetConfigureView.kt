package io.github.wulkanowy.ui.modules.luckynumberwidget

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface LuckyNumberWidgetConfigureView : BaseView {

    fun initView()

    fun updateData(data: List<StudentWithSemesters>, selectedStudentId: Long)

    fun updateLuckyNumberWidget(widgetId: Int)

    fun setSuccessResult(widgetId: Int)

    fun finishView()

    fun openLoginView()
}
