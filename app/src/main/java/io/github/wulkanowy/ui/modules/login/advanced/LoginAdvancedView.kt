package io.github.wulkanowy.ui.modules.login.advanced

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginAdvancedView : BaseView {

    val formNameValue: String

    val formPassValue: String

    val formHostValue: String?

    val formLoginType: String

    val formPinValue: String

    val formSymbolValue: String

    val formTokenValue: String

    fun initView()

    fun setDefaultCredentials(name: String, pass: String, symbol: String, token: String, pin: String)

    fun setErrorNameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun clearNameError()

    fun clearPassError()

    fun clearPinKeyError()

    fun clearSymbolError()

    fun clearTokenError()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun notifyParentAccountLogged(students: List<Student>)

    fun setErrorPinRequired()

    fun setErrorPinInvalid(message: String)

    fun setErrorSymbolRequired()

    fun setErrorSymbolInvalid(message: String)

    fun setErrorTokenRequired()

    fun setErrorTokenInvalid(message: String)

    fun showOnlyHybridModeInputs()

    fun showOnlyScrapperModeInputs()

    fun showOnlyMobileApiModeInputs()
}
