package io.github.wulkanowy.utils

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.BuildConfig
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) Crashlytics.log(message)
        else Crashlytics.logException(t)
    }
}

fun initCrashlytics(context: Context) {
    Fabric.with(Fabric.Builder(context).kits(
        Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled(!BuildConfig.CRASHLYTICS_ENABLED).build()).build()
    ).debuggable(BuildConfig.DEBUG).build())
}
