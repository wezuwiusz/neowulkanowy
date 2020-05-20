package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface AccountView : BaseView {

    fun initView()

    fun updateData(data: List<Student>)

    fun dismissView()

    fun showConfirmDialog()

    fun openLoginView()

    fun recreateMainView()
}

