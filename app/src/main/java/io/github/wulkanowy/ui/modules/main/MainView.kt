package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    var startMenuIndex: Int

    val isRootView: Boolean

    val currentViewTitle: String?

    val currentStackSize: Int?

    fun initView()

    fun switchMenuView(position: Int)

    fun showHomeArrow(show: Boolean)

    fun showAccountPicker()

    fun showExpiredDialog()

    fun notifyMenuViewReselected()

    fun setViewTitle(title: String)

    fun popView()

    fun cancelNotifications()

    fun openLoginView()

    interface MainChildView {

        fun onFragmentReselected()
    }

    interface TitledView {

        val titleStringId: Int
    }
}
