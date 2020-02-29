package io.github.wulkanowy.ui.modules.login.advanced

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginAdvancedView : BaseView {

    val formUsernameValue: String

    val formPassValue: String

    val formHostValue: String

    val formHostSymbol: String

    val formLoginType: String

    val formPinValue: String

    val formSymbolValue: String

    val formTokenValue: String

    val nicknameLabel: String

    val emailLabel: String

    fun initView()

    fun setDefaultCredentials(username: String, pass: String, symbol: String, token: String, pin: String)

    fun setUsernameLabel(label: String)

    fun setSymbol(symbol: String)

    fun setErrorUsernameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun clearUsernameError()

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
