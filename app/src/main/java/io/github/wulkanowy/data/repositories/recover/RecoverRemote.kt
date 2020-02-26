package io.github.wulkanowy.data.repositories.recover

import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRemote @Inject constructor(private val sdk: Sdk) {

    fun getReCaptchaSiteKey(host: String, symbol: String): Single<Pair<String, String>> {
        return sdk.getPasswordResetCaptchaCode(host, symbol)
    }

    fun sendRecoverRequest(url: String, symbol: String, email: String, reCaptchaResponse: String): Single<String> {
        return sdk.sendPasswordResetRequest(url, symbol, email, reCaptchaResponse)
    }
}

