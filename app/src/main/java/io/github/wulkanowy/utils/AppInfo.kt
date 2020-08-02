package io.github.wulkanowy.utils

import android.content.res.Resources
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.VERSION.SDK_INT
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.BuildConfig.VERSION_CODE
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppInfo @Inject constructor() {

    open val isDebug get() = DEBUG

    open val versionCode get() = VERSION_CODE

    open val versionName get() = VERSION_NAME

    open val systemVersion get() = SDK_INT

    open val systemManufacturer: String get() = MANUFACTURER

    open val systemModel: String get() = MODEL

    @Suppress("DEPRECATION")
    open val systemLanguage: String
        get() = Resources.getSystem().configuration.locale.language
}
