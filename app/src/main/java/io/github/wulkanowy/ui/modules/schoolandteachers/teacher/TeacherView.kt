package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView

interface TeacherView : BaseView, SchoolAndTeachersChildView {

    val isViewEmpty: Boolean

    val noSubjectString: String

    fun initView()

    fun updateData(data: List<TeacherItem>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun hideRefresh()

    fun clearData()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)
}
