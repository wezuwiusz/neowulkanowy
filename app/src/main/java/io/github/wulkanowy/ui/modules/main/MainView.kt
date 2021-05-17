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

    enum class Section(val id: Int) {
        GRADE(0),
        ATTENDANCE(1),
        EXAM(2),
        TIMETABLE(3),
        MORE(4),
        MESSAGE(5),
        HOMEWORK(6),
        NOTE(7),
        LUCKY_NUMBER(8),
        SETTINGS(9),
        ABOUT(10),
        SCHOOL(11),
        ACCOUNT(12),
        STUDENT_INFO(13),
        CONFERENCE(14),
        SCHOOL_ANNOUNCEMENT(15)
    }
}
