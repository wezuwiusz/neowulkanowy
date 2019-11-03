package io.github.wulkanowy

import android.content.Context
import android.util.Log.INFO
import android.util.Log.VERBOSE
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yariksoffice.lingver.Lingver
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.services.sync.SyncWorkerFactory
import io.github.wulkanowy.ui.base.ThemeManager
import io.github.wulkanowy.utils.ActivityLifecycleLogger
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.CrashlyticsTree
import io.github.wulkanowy.utils.DebugLogTree
import io.github.wulkanowy.utils.initCrashlytics
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class WulkanowyApp : DaggerApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: SyncWorkerFactory

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var appInfo: AppInfo

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        RxJavaPlugins.setErrorHandler(::onError)
        Lingver.init(this)
        themeManager.applyDefaultTheme()

        initLogging()
        initCrashlytics(this, appInfo)
    }

    private fun initLogging() {
        if (appInfo.isDebug) {
            Timber.plant(DebugLogTree())
            FlexibleAdapter.enableLogs(Log.Level.DEBUG)
        } else {
            Timber.plant(CrashlyticsTree())
        }
        registerActivityLifecycleCallbacks(ActivityLifecycleLogger())
    }

    private fun onError(error: Throwable) {
        //RxJava's too deep stack traces may cause SOE on older android devices
        val cause = error.cause
        if (error is UndeliverableException && cause is IOException || cause is InterruptedException || cause is StackOverflowError) {
            Timber.e(cause, "An undeliverable error occurred")
        } else throw error
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(if (appInfo.isDebug) VERBOSE else INFO)
        .build()
}
