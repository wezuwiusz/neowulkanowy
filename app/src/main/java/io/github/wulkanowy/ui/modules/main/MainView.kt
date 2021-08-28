package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    var startMenuMoreIndex: Int

    val isRootView: Boolean

    val currentViewTitle: String?

    val currentViewSubtitle: String?

    val currentStackSize: Int?

    fun initView()

    fun switchMenuView(position: Int)

    fun showHomeArrow(show: Boolean)

    fun showAccountPicker(studentWithSemesters: List<StudentWithSemesters>)

    fun showActionBarElevation(show: Boolean)

    fun notifyMenuViewReselected()

    fun notifyMenuViewChanged()

    fun setViewTitle(title: String)

    fun setViewSubTitle(subtitle: String?)

    fun popView(depth: Int = 1)

    fun showStudentAvatar(student: Student)

    interface MainChildView {

        fun onFragmentReselected()

        fun onFragmentChanged() {}
    }

    interface TitledView {

        val titleStringId: Int

        var subtitleString: String
            get() = ""
            set(_) {}
    }

    enum class Section {
        DASHBOARD,
        GRADE,
        ATTENDANCE,
        TIMETABLE,
        MORE,
        MESSAGE,
        EXAM,
        HOMEWORK,
        NOTE,
        CONFERENCE,
        SCHOOL_ANNOUNCEMENT,
        SCHOOL,
        LUCKY_NUMBER,
        ACCOUNT,
        STUDENT_INFO,
        SETTINGS;

        val id get() = ordinal
    }
}
