package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.sdk.Sdk
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val sdk: Sdk) {

    suspend fun getReCaptchaSiteKey(host: String, symbol: String): Pair<String, String> {
        return sdk.getPasswordResetCaptchaCode(host, symbol)
    }

    suspend fun sendRecoverRequest(
        url: String, symbol: String, email: String, reCaptchaResponse: String
    ): String = sdk.sendPasswordResetRequest(url, symbol, email, reCaptchaResponse)
}
