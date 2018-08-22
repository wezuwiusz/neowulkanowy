package io.github.wulkanowy

import android.content.Context
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.utils.LoggerUtils
import timber.log.Timber

class WulkanowyApp : DaggerApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            enableDebugLog()
        }
        initializeFabric()
    }

    private fun enableDebugLog() {
        FlexibleAdapter.enableLogs(eu.davidea.flexibleadapter.utils.Log.Level.DEBUG)
        Timber.plant(LoggerUtils.DebugLogTree())
    }

    private fun initializeFabric() {
        Fabric.with(Fabric.Builder(this)
                .kits(
                        Crashlytics.Builder()
                                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                                .build(),
                        Answers()
                )
                .debuggable(BuildConfig.DEBUG)
                .build())
        Timber.plant(LoggerUtils.CrashlyticsTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
            DaggerAppComponent.builder().create(this)
}
