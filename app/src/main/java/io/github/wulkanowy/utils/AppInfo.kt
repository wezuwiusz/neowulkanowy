package io.github.wulkanowy.utils

import android.content.res.Resources
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.VERSION.SDK_INT
import io.github.wulkanowy.BuildConfig.BUILD_TIMESTAMP
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.BuildConfig.FLAVOR
import io.github.wulkanowy.BuildConfig.VERSION_CODE
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppInfo @Inject constructor() {

    open val isDebug get() = DEBUG

    open val versionCode get() = VERSION_CODE

    open val buildTimestamp get() = BUILD_TIMESTAMP

    open val buildFlavor get() = FLAVOR

    open val versionName get() = VERSION_NAME

    open val systemVersion get() = SDK_INT

    open val systemManufacturer: String get() = MANUFACTURER

    open val systemModel: String get() = MODEL

    @Suppress("DEPRECATION")
    open val systemLanguage: String
        get() = Resources.getSystem().configuration.locale.language

    val defaultColorsForAvatar = listOf(
        0xd32f2f, 0xE64A19, 0xFFA000, 0xAFB42B, 0x689F38, 0x388E3C, 0x00796B, 0x0097A7,
        0x1976D2, 0x3647b5, 0x6236c9, 0x9225c1, 0xC2185B, 0x616161, 0x455A64, 0x7a5348
    ).map { (it and 0x00ffffff or (255 shl 24)).toLong() }
}
