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

    open val defaultColorsForAvatar = listOf(
        0xe57373, 0xf06292, 0xba68c8, 0x9575cd, 0x7986cb, 0x64b5f6, 0x4fc3f7, 0x4dd0e1, 0x4db6ac,
        0x81c784, 0xaed581, 0xff8a65, 0xd4e157, 0xffd54f, 0xffb74d, 0xa1887f, 0x90a4ae
    )
}
