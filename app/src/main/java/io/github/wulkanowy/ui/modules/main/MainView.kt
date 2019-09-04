package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    var startMenuMoreIndex: Int

    val isRootView: Boolean

    val currentViewTitle: String?

    val currentStackSize: Int?

    fun initView()

    fun switchMenuView(position: Int)

    fun showHomeArrow(show: Boolean)

    fun showAccountPicker()

    fun showActionBarElevation(show: Boolean)

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    fun popView(depth: Int = 1)

    interface MainChildView {

        fun onFragmentReselected()
    }

    interface TitledView {

        val titleStringId: Int
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
        ABOUT(10)
    }
}
