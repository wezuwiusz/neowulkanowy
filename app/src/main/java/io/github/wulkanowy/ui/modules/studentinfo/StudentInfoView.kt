package io.github.wulkanowy.ui.modules.studentinfo

import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface StudentInfoView : BaseView {

    enum class Type {
        PERSONAL, ADDRESS, CONTACT, FAMILY, FIRST_GUARDIAN, SECOND_GUARDIAN
    }

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<Pair<String, String>>)

    fun showPersonalTypeData(studentInfo: StudentInfo)

    fun showContactTypeData(studentInfo: StudentInfo)

    fun showAddressTypeData(studentInfo: StudentInfo)

    fun showFamilyTypeData(studentInfo: StudentInfo)

    fun showFirstGuardianTypeData(studentInfo: StudentInfo)

    fun showSecondGuardianTypeData(studentInfo: StudentInfo)

    fun openStudentInfoView(infoType: Type, studentWithSemesters: StudentWithSemesters)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun hideRefresh()

    fun copyToClipboard(text: String)
}
