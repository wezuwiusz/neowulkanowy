package io.github.wulkanowy.utils

import android.content.res.Resources
import io.github.wulkanowy.R
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.NotLoggedInException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import okhttp3.internal.http2.StreamResetException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Resources.getString(error: Throwable) = when (error) {
    is UnknownHostException -> getString(R.string.error_no_internet)
    is SocketException,
    is SocketTimeoutException,
    is InterruptedIOException,
    is ConnectException,
    is StreamResetException -> getString(R.string.error_timeout)
    is NotLoggedInException -> getString(R.string.error_login_failed)
    is PasswordChangeRequiredException -> getString(R.string.error_password_change_required)
    is ServiceUnavailableException -> getString(R.string.error_service_unavailable)
    is FeatureDisabledException -> getString(R.string.error_feature_disabled)
    is FeatureNotAvailableException -> getString(R.string.error_feature_not_available)
    is VulcanException -> getString(R.string.error_unknown_uonet)
    is ScrapperException -> getString(R.string.error_unknown_app)
    else -> getString(R.string.error_unknown)
}
