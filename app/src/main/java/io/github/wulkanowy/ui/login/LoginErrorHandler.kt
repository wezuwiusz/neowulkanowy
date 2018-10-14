package io.github.wulkanowy.ui.login

import android.content.res.Resources
import io.github.wulkanowy.api.login.BadCredentialsException
import io.github.wulkanowy.data.ErrorHandler

class LoginErrorHandler(resources: Resources) : ErrorHandler(resources) {

    var doOnBadCredentials: () -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is BadCredentialsException -> doOnBadCredentials()
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        doOnBadCredentials = {}
    }
}

