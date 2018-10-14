package io.github.wulkanowy.ui.main

import io.github.wulkanowy.ui.base.BaseView

interface MainView : BaseView {

    fun initView()

    fun switchMenuView(position: Int)

    fun setViewTitle(title: String)

    fun viewTitle(index: Int): String

    fun currentMenuIndex(): Int

    fun notifyMenuViewReselected()

    interface MenuFragmentView {

        fun onFragmentReselected()
    }
}
