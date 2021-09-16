package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView

interface AccountDetailsView : BaseView {

    fun initView()

    fun showAccountData(student: Student)

    fun showAccountEditDetailsDialog(student: Student)

    fun showLogoutConfirmDialog()

    fun popViewToMain()

    fun popViewToAccounts()

    fun recreateMainView()

    fun enableSelectStudentButton(enable: Boolean)

    fun openStudentInfoView(
        infoType: StudentInfoView.Type,
        studentWithSemesters: StudentWithSemesters
    )

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)
}
