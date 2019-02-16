package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginFormView : BaseView {

    fun initView()

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

    fun notifyParentAccountLogged(students: List<Student>)
}
