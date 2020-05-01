package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView

interface TeacherView : BaseView, SchoolAndTeachersChildView {

    val isViewEmpty: Boolean

    val noSubjectString: String

    fun initView()

    fun updateData(data: List<Teacher>)

    fun hideRefresh()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)
}
