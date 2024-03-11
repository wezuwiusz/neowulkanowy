package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(
    private val wulkanowySdkFactory: WulkanowySdkFactory
) {

    suspend fun getReCaptchaSiteKey(host: String, symbol: String): Pair<String, String> =
        wulkanowySdkFactory.create()
            .getPasswordResetCaptchaCode(host, symbol)

    suspend fun sendRecoverRequest(
        url: String,
        symbol: String,
        email: String,
        reCaptchaResponse: String
    ): String = wulkanowySdkFactory.create()
        .sendPasswordResetRequest(url, symbol, email, reCaptchaResponse)
}
