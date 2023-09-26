package io.github.wulkanowy.ui.modules.login.support

import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.ui.modules.login.LoginData
import java.io.Serializable

data class LoginSupportInfo(
    val loginData: LoginData,
    val registerUser: RegisterUser?,
    val lastErrorMessage: String?,
    val enteredSymbol: String?,
) : Serializable
