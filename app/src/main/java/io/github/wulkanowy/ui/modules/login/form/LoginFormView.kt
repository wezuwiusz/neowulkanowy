package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initInputs()

    fun setErrorNicknameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorSymbolRequire()

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun setErrorSymbolIncorrect()

    fun resetViewErrors()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showLoginProgress(show: Boolean)

    fun showSymbolInput()

    fun switchNextView()
}
