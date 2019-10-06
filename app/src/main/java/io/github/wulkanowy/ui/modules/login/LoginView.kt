package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginView : BaseView {

    val currentViewIndex: Int

    fun initView()

    fun switchView(index: Int)

    fun showActionBar(show: Boolean)

    fun notifyInitSymbolFragment(loginData: Triple<String, String, String>)

    fun notifyInitStudentSelectFragment(students: List<Student>)
}
