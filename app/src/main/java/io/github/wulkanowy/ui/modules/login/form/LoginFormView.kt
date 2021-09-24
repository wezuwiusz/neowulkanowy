package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initView()

    val formUsernameValue: String

    val formPassValue: String

    val formHostValue: String

    val formHostSymbol: String

    val nicknameLabel: String

    val emailLabel: String

    fun getHostsValues(): List<String>

    fun setCredentials(username: String, pass: String)

    fun setHost(host: String)

    fun setUsernameLabel(label: String)

    fun setErrorUsernameRequired()

    fun setErrorLoginRequired()

    fun setErrorEmailRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect(message: String?)

    fun setErrorEmailInvalid(domain: String)

    fun clearUsernameError()

    fun clearPassError()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showVersion()

    fun notifyParentAccountLogged(studentsWithSemesters: List<StudentWithSemesters>, loginData: Triple<String, String, String>)

    fun openPrivacyPolicyPage()

    fun showContact(show: Boolean)

    fun openFaqPage()

    fun openEmail(lastError: String)

    fun openAdvancedLogin()

    fun onRecoverClick()
}
