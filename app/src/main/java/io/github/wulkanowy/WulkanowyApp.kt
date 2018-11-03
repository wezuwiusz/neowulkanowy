package io.github.wulkanowy

import android.content.Context
import androidx.multidex.MultiDex
import com.akaita.java.rxjava2debug.RxJava2Debug
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.utils.CrashlyticsTree
import io.github.wulkanowy.utils.DebugLogTree
import timber.log.Timber

class WulkanowyApp : DaggerApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initializeFabric()
        if (DEBUG) enableDebugLog()
        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf(BuildConfig.APPLICATION_ID))
    }

    private fun enableDebugLog() {
        Timber.plant(DebugLogTree())
        FlexibleAdapter.enableLogs(Log.Level.DEBUG)
    }

    private fun initializeFabric() {
        Fabric.with(Fabric.Builder(this).kits(
            Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG || !BuildConfig.FABRIC_ENABLED).build()).build(),
            Answers()
        ).debuggable(BuildConfig.DEBUG).build())
        Timber.plant(CrashlyticsTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}
