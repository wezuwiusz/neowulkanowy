package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initInputs()

    fun setErrorEmailRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorSymbolRequire()

    fun setErrorEmailInvalid()

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