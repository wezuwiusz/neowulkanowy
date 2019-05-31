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

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    fun popView()

    interface MainChildView {

        fun onFragmentReselected()
    }

    interface TitledView {

        val titleStringId: Int
    }

    enum class MenuView(val id: Int) {
        GRADE(0),
        ATTENDANCE(1),
        EXAM(2),
        TIMETABLE(3),
        MESSAGE(4),
        HOMEWORK(5),
        NOTE(6),
        LUCKY_NUMBER(7),
    }
}
