package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView

interface AccountDetailsView : BaseView {

    fun initView()

    fun showAccountData(studentWithSemesters: StudentWithSemesters)

    fun showAccountEditDetailsDialog()

    fun showLogoutConfirmDialog()

    fun popView()

    fun recreateMainView()

    fun openStudentInfoView(
        infoType: StudentInfoView.Type,
        studentWithSemesters: StudentWithSemesters
    )
}
