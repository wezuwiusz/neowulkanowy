package io.github.wulkanowy.ui.modules.schoolandteachers.school

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView

interface SchoolView : BaseView, SchoolAndTeachersChildView {

    fun initView()

    fun updateData(data: School)

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun hideRefresh()
}
