package io.github.wulkanowy.utils

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import fr.bipi.tressence.crash.CrashlyticsLogExceptionTree
import fr.bipi.tressence.crash.CrashlyticsLogTree
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import java.net.UnknownHostException

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

class CrashlyticsTree : CrashlyticsLogTree(Log.VERBOSE) {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t is FeatureDisabledException || t is FeatureNotAvailableException || t is UnknownHostException) return

        super.log(priority, tag, message, t)
    }
}

class CrashlyticsExceptionTree : CrashlyticsLogExceptionTree()
