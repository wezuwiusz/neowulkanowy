package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initView()

    val formUsernameValue: String

    val formPassValue: String

    val formHostValue: String

    val nicknameLabel: String

    val emailLabel: String

    fun setCredentials(username: String, pass: String)

    fun setUsernameLabel(label: String)

    fun setErrorUsernameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun clearUsernameError()

    fun clearPassError()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showVersion()

    fun notifyParentAccountLogged(students: List<Student>, loginData: Triple<String, String, String>)

    fun openPrivacyPolicyPage()

    fun showContact(show: Boolean)

    fun openFaqPage()

    fun openEmail()

    fun openAdvancedLogin()

    fun onRecoverClick()
}
