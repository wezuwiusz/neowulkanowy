package io.github.wulkanowy.utils

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import timber.log.Timber

fun initCrashlytics(context: Context, appInfo: AppInfo) {
    Fabric.with(Fabric.Builder(context)
        .kits(
            Crashlytics.Builder()
                .core(CrashlyticsCore.Builder()
                    .disabled(!appInfo.isCrashlyticsEnabled)
                    .build())
                .build()
        )
        .debuggable(appInfo.isDebug)
        .build())
}

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) Crashlytics.log(message)
        else Crashlytics.logException(t)
    }
}
