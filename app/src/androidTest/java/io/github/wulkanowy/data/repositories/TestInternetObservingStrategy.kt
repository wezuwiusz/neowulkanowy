package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingStrategy
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler
import io.reactivex.Observable
import io.reactivex.Single

class TestInternetObservingStrategy : InternetObservingStrategy {

    override fun checkInternetConnectivity(host: String?, port: Int, timeoutInMs: Int, httpResponse: Int, errorHandler: ErrorHandler?): Single<Boolean> {
        return Single.just(true)
    }

    override fun observeInternetConnectivity(initialIntervalInMs: Int, intervalInMs: Int, host: String?, port: Int, timeoutInMs: Int, httpResponse: Int, errorHandler: ErrorHandler?): Observable<Boolean> {
        return Observable.just(true)
    }

    override fun getDefaultPingHost() = "localhost"
}
