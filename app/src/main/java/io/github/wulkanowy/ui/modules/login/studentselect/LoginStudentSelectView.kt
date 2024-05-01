package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.login.LoginData
import io.github.wulkanowy.ui.modules.login.support.LoginSupportInfo

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

    fun openEmail(supportInfo: LoginSupportInfo)

    fun showAdminMessage(adminMessage: AdminMessage?)

    fun openInternetBrowser(url: String)
}
