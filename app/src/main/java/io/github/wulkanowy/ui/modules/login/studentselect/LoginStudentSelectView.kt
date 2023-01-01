package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.login.LoginData

interface LoginStudentSelectView : BaseView {

    val symbols: Map<String, String>

    fun initView()

    fun updateData(data: List<LoginStudentSelectItem>)

    fun navigateToSymbol(loginData: LoginData)

    fun navigateToNext()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun enableSignIn(enable: Boolean)

    fun openDiscordInvite()

    fun openEmail(lastError: String)
}
