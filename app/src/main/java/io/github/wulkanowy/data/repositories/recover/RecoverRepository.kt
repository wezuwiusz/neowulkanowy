package io.github.wulkanowy.data.repositories.recover

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoverRepository @Inject constructor(private val settings: InternetObservingSettings, private val remote: RecoverRemote) {

    fun getReCaptchaSiteKey(host: String, symbol: String): Single<Pair<String, String>> {
        return ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
            if (it) remote.getReCaptchaSiteKey(host, symbol)
            else Single.error(UnknownHostException())
        }
    }

    fun sendRecoverRequest(url: String, symbol: String, email: String, reCaptchaResponse: String): Single<String> {
        return ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
            if (it) remote.sendRecoverRequest(url, symbol, email, reCaptchaResponse)
            else Single.error(UnknownHostException())
        }
    }
}
