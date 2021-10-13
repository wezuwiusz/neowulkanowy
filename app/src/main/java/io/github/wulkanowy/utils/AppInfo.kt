package io.github.wulkanowy.utils

import android.content.res.Resources
import android.os.Build
import io.github.wulkanowy.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppInfo @Inject constructor() {

    open val isDebug get() = BuildConfig.DEBUG

    open val versionCode get() = BuildConfig.VERSION_CODE

    open val buildTimestamp get() = BuildConfig.BUILD_TIMESTAMP

    open val buildFlavor get() = BuildConfig.FLAVOR

    open val versionName get() = BuildConfig.VERSION_NAME

    open val systemVersion get() = Build.VERSION.SDK_INT

    open val systemManufacturer: String get() = Build.MANUFACTURER

    open val systemModel: String get() = Build.MODEL

    open val messagesBaseUrl = BuildConfig.MESSAGES_BASE_URL

    @Suppress("DEPRECATION")
    open val systemLanguage: String
        get() = Resources.getSystem().configuration.locale.language

    val defaultColorsForAvatar = listOf(
        0xd32f2f, 0xE64A19, 0xFFA000, 0xAFB42B, 0x689F38, 0x388E3C, 0x00796B, 0x0097A7,
        0x1976D2, 0x3647b5, 0x6236c9, 0x9225c1, 0xC2185B, 0x616161, 0x455A64, 0x7a5348
    ).map { (it and 0x00ffffff or (255 shl 24)).toLong() }
}
