package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface AccountView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<AccountItem<*>>)

    fun openLoginView()

    fun openAccountDetailsView(studentWithSemesters: StudentWithSemesters)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)
}

