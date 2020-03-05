package io.github.wulkanowy.utils

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import fr.bipi.tressence.crash.CrashlyticsLogExceptionTree
import fr.bipi.tressence.crash.CrashlyticsLogTree
import io.fabric.sdk.android.Fabric

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

class CrashlyticsTree : CrashlyticsLogTree(Log.VERBOSE)

class CrashlyticsExceptionTree : CrashlyticsLogExceptionTree()
