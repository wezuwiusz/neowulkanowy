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
import java.security.cert.CertPathValidatorException
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import javax.net.ssl.SSLHandshakeException

fun Resources.getErrorString(error: Throwable): String = when (error) {
    is UnknownHostException -> R.string.error_no_internet
    is ConnectException,
    is SocketException,
    is SocketTimeoutException,
    is InterruptedIOException,
    is StreamResetException -> R.string.error_timeout
    is NotLoggedInException -> R.string.error_login_failed
    is PasswordChangeRequiredException -> R.string.error_password_change_required
    is ServiceUnavailableException -> R.string.error_service_unavailable
    is FeatureDisabledException -> R.string.error_feature_disabled
    is FeatureNotAvailableException -> R.string.error_feature_not_available
    is VulcanException -> R.string.error_unknown_uonet
    is ScrapperException -> R.string.error_unknown_app
    is SSLHandshakeException -> when {
        error.isCausedByCertificateNotValidNow() -> R.string.error_invalid_device_datetime
        else -> R.string.error_timeout
    }
    else -> R.string.error_unknown
}.let { getString(it) }

fun Throwable.isShouldBeReported(): Boolean = when (this) {
    is UnknownHostException,
    is ConnectException,
    is SocketException,
    is SocketTimeoutException,
    is InterruptedIOException,
    is StreamResetException,
    is ServiceUnavailableException,
    is FeatureDisabledException,
    is FeatureNotAvailableException -> false
    is SSLHandshakeException -> when {
        isCausedByCertificateNotValidNow() -> false
        else -> true
    }
    else -> true
}

private fun Throwable?.isCausedByCertificateNotValidNow(): Boolean {
    var exception = this
    do {
        if (exception.isCertificateNotValidNow()) return true

        exception = exception?.cause
    } while (exception != null)
    return false
}

private fun Throwable?.isCertificateNotValidNow(): Boolean {
    val isNotYetValid = this is CertificateNotYetValidException
    val isExpired = this is CertificateExpiredException
    val isInvalidPath = this is CertPathValidatorException
    return isNotYetValid || isExpired || isInvalidPath
}
