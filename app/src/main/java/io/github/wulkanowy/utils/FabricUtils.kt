package io.github.wulkanowy.utils

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.crashlytics.android.answers.SignUpEvent
import timber.log.Timber
import kotlin.math.min

fun logLogin(method: String) {
    try {
        Answers.getInstance().logLogin(LoginEvent().putMethod(method))
    } catch (e: Throwable) {
        Timber.d(e)
    }
}

fun logRegister(message: String, result: Boolean, symbol: String, endpoint: String) {
    try {
        Answers.getInstance().logSignUp(SignUpEvent()
            .putMethod("Login activity")
            .putSuccess(result)
            .putCustomAttribute("symbol", symbol)
            .putCustomAttribute("message", message.substring(0, min(message.length, 100)))
            .putCustomAttribute("endpoint", endpoint)
        )
    } catch (e: Throwable) {
        Timber.d(e)
    }
}

fun <T> logEvent(name: String, params: Map<String, T>) {
    try {
        Answers.getInstance().logCustom(CustomEvent(name)
            .apply {
                params.forEach {
                    when {
                        it.value is String -> putCustomAttribute(it.key, it.value as String)
                        it.value is Number -> putCustomAttribute(it.key, it.value as Number)
                        it.value is Boolean -> putCustomAttribute(it.key, if ((it.value as Boolean)) "true" else "false")
                        else -> Timber.w("logEvent() unknown value type: ${it.value}")
                    }
                }
            }
        )
    } catch (e: Throwable) {
        Timber.d(e)
    }
}
