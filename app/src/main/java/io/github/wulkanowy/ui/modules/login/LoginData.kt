package io.github.wulkanowy.ui.modules.login

import java.io.Serializable

data class LoginData(
    val login: String,
    val password: String,
    val baseUrl: String,
    val symbol: String?,
) : Serializable
