package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    val isDebug: Boolean

    fun initView()

    fun switchOptionsView()

    fun setErrorNicknameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorSymbolRequire()

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun setErrorSymbolIncorrect()

    fun resetViewErrors()

    fun showVersion()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showSymbolInput()
}
