package io.github.wulkanowy.data

import android.content.res.Resources
import io.github.wulkanowy.R
import io.github.wulkanowy.api.NotLoggedInErrorException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

open class ErrorHandler @Inject constructor(private val resources: Resources) {

    var showErrorMessage: (String) -> Unit = {}

    open fun proceed(error: Throwable) {
        Timber.i(error, "An exception occurred while the Wulkanowy was running")

        showErrorMessage((when (error) {
            is UnknownHostException -> resources.getString(R.string.noInternet_text)
            is SocketTimeoutException -> resources.getString(R.string.generic_timeout_error)
            is NotLoggedInErrorException, is IOException -> resources.getString(R.string.login_failed_text)
            else -> error.localizedMessage
        }))
    }

    open fun clear() {
        showErrorMessage = {}
    }
}

