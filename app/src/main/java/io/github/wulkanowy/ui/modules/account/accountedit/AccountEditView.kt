package io.github.wulkanowy.ui.modules.account.accountedit

import io.github.wulkanowy.ui.base.BaseView

interface AccountEditView : BaseView {

    fun initView()

    fun popView()

    fun recreateMainView()

    fun showCurrentNick(nick: String)

    fun updateSelectedColorData(color: Int)

    fun updateColorsData(colors: List<Int>)
}
