package io.github.wulkanowy.data

import android.content.res.Resources
import io.github.wulkanowy.R
import io.github.wulkanowy.api.interceptor.ServiceUnavailableException
import io.github.wulkanowy.api.login.NotLoggedInException
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

open class ErrorHandler @Inject constructor(protected val resources: Resources) {

    var showErrorMessage: (String, Throwable) -> Unit = { _, _ -> }

    open fun proceed(error: Throwable) {
        Timber.e(error, "An exception occurred while the Wulkanowy was running")

        showErrorMessage((when (error) {
            is UnknownHostException -> resources.getString(R.string.error_no_internet)
            is SocketTimeoutException -> resources.getString(R.string.error_timeout)
            is NotLoggedInException -> resources.getString(R.string.error_login_failed)
            is ServiceUnavailableException -> resources.getString(R.string.error_service_unavaible)
            else -> resources.getString(R.string.error_unknown)
        }), error)
    }

    open fun clear() {
        showErrorMessage = { _, _ -> }
    }
}
