package io.github.wulkanowy.utils

import android.content.res.Resources
import io.github.wulkanowy.R
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.exception.NotLoggedInException
import io.github.wulkanowy.sdk.exception.ServiceUnavailableException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Resources.getString(error: Throwable) = when (error) {
    is UnknownHostException -> getString(R.string.error_no_internet)
    is SocketTimeoutException -> getString(R.string.error_timeout)
    is NotLoggedInException -> getString(R.string.error_login_failed)
    is ServiceUnavailableException -> getString(R.string.error_service_unavailable)
    is FeatureDisabledException -> getString(R.string.error_feature_disabled)
    is FeatureNotAvailableException -> getString(R.string.error_feature_not_available)
    else -> getString(R.string.error_unknown)
}
