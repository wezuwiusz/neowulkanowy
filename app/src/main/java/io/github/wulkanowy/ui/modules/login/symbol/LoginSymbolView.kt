package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginSymbolView : BaseView {

    val symbolNameError: CharSequence?

    fun initView()

    fun setErrorSymbolIncorrect()

    fun setErrorSymbolRequire()

    fun clearSymbolError()

    fun clearAndFocusSymbol()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun notifyParentAccountLogged(students: List<Student>)
}
