package io.github.wulkanowy.ui.modules.main

import android.content.res.Resources
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.utils.security.ScramblerException
import javax.inject.Inject

@PerActivity
class MainErrorHandler @Inject constructor(resources: Resources) : ErrorHandler(resources) {

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
