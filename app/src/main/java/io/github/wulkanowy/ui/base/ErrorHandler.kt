package io.github.wulkanowy.ui.base

import android.content.res.Resources
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException
import io.github.wulkanowy.utils.getString
import io.github.wulkanowy.utils.security.ScramblerException
import timber.log.Timber
import javax.inject.Inject

open class ErrorHandler @Inject constructor(protected val resources: Resources) {

    var showErrorMessage: (String, Throwable) -> Unit = { _, _ -> }

    var onSessionExpired: () -> Unit = {}

    var onNoCurrentStudent: () -> Unit = {}

    fun dispatch(error: Throwable) {
        Timber.e(error, "An exception occurred while the Wulkanowy was running")
        proceed(error)
    }

    protected open fun proceed(error: Throwable) {
        showErrorMessage(resources.getString(error), error)
        when (error) {
            is ScramblerException, is BadCredentialsException -> onSessionExpired()
            is NoCurrentStudentException -> onNoCurrentStudent()
        }
    }

    open fun clear() {
        showErrorMessage = { _, _ -> }
        onSessionExpired = {}
        onNoCurrentStudent = {}
    }
}
