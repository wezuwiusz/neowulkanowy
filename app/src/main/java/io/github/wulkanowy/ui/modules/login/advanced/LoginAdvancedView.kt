package io.github.wulkanowy.ui.modules.login.advanced

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.login.LoginData

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

    fun showMobileApiWarningMessage()

    fun showScraperWarningMessage()

    fun showHybridWarningMessage()

    fun setDefaultCredentials(username: String, pass: String, symbol: String, token: String, pin: String)

    fun setUsernameLabel(label: String)

    fun setSymbol(symbol: String)

    fun setErrorUsernameRequired()

    fun setErrorLoginRequired()

    fun setErrorEmailRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect(message: String?)

    fun clearUsernameError()

    fun clearPassError()

    fun clearPinKeyError()

    fun clearSymbolError()

    fun clearTokenError()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun navigateToSymbol(loginData: LoginData)

    fun navigateToStudentSelect(loginData: LoginData, registerUser: RegisterUser)

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
