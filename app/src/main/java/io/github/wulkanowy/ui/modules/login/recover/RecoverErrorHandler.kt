package io.github.wulkanowy.ui.modules.login.recover

import android.content.res.Resources
import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.sdk.scrapper.exception.InvalidCaptchaException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidEmailException
import io.github.wulkanowy.sdk.scrapper.exception.NoAccountFoundException
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class RecoverErrorHandler @Inject constructor(
    resources: Resources,
    chuckCollector: ChuckCollector
) : ErrorHandler(resources, chuckCollector) {

    var onInvalidUsername: (String) -> Unit = {}

    var onInvalidCaptcha: (String, Throwable) -> Unit = { _, _ -> }

    override fun proceed(error: Throwable) {
        when (error) {
            is InvalidEmailException, is NoAccountFoundException -> onInvalidUsername(error.localizedMessage.orEmpty())
            is InvalidCaptchaException -> onInvalidCaptcha(error.localizedMessage.orEmpty(), error)
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onInvalidUsername = {}
    }
}
