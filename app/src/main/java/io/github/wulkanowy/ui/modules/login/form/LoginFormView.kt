package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initView()

    val formNameValue: String

    val formPassValue: String

    val formHostValue: String?

    fun setCredentials(name: String, pass: String)

    fun setErrorNameRequired()

    fun setErrorPassRequired(focus: Boolean)

    fun setErrorPassInvalid(focus: Boolean)

    fun setErrorPassIncorrect()

    fun clearNameError()

    fun clearPassError()

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showVersion()

    fun showPrivacyPolicy()

    fun notifyParentAccountLogged(students: List<Student>, loginData: Triple<String, String, String>)

    fun openPrivacyPolicyPage()
}
