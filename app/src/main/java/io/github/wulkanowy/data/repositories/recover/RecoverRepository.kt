package io.github.wulkanowy.data.repositories.recover

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val remote: RecoverRemote) {

    suspend fun getReCaptchaSiteKey(host: String, symbol: String): Pair<String, String> {
        return remote.getReCaptchaSiteKey(host, symbol)
    }

    suspend fun sendRecoverRequest(url: String, symbol: String, email: String, reCaptchaResponse: String): String {
        return remote.sendRecoverRequest(url, symbol, email, reCaptchaResponse)
    }
}
