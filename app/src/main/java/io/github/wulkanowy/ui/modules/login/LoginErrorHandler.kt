package io.github.wulkanowy.ui.modules.login

import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import io.github.wulkanowy.R
import io.github.wulkanowy.sdk.mobile.exception.InvalidPinException
import io.github.wulkanowy.sdk.mobile.exception.InvalidSymbolException
import io.github.wulkanowy.sdk.mobile.exception.InvalidTokenException
import io.github.wulkanowy.sdk.mobile.exception.TokenDeadException
import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class LoginErrorHandler @Inject constructor(resources: Resources) : ErrorHandler(resources) {

    var onBadCredentials: () -> Unit = {}

    var onInvalidToken: (String) -> Unit = {}

    var onInvalidPin: (String) -> Unit = {}

    var onInvalidSymbol: (String) -> Unit = {}

    var onStudentDuplicate: (String) -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is BadCredentialsException -> onBadCredentials()
            is SQLiteConstraintException -> onStudentDuplicate(resources.getString(R.string.login_duplicate_student))
            is TokenDeadException -> onInvalidToken(resources.getString(R.string.login_expired_token))
            is InvalidTokenException -> onInvalidToken(resources.getString(R.string.login_invalid_token))
            is InvalidPinException -> onInvalidPin(resources.getString(R.string.login_invalid_pin))
            is InvalidSymbolException -> onInvalidSymbol(resources.getString(R.string.login_invalid_symbol))
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onBadCredentials = {}
        onStudentDuplicate = {}
        onInvalidToken = {}
        onInvalidPin = {}
        onInvalidSymbol = {}
    }
}
