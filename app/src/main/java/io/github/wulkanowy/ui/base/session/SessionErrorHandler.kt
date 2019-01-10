package io.github.wulkanowy.ui.base.session

import android.content.res.Resources
import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.security.ScramblerException
import javax.inject.Inject

class SessionErrorHandler @Inject constructor(resources: Resources, chuckCollector: ChuckCollector) : ErrorHandler(resources, chuckCollector) {

    var onDecryptionFail: () -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is ScramblerException -> onDecryptionFail()
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onDecryptionFail = {}
    }
}
