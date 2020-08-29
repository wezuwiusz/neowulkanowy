package io.github.wulkanowy

import android.app.Application
import android.content.Context
import android.util.Log.DEBUG
import android.util.Log.INFO
import android.util.Log.VERBOSE
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import fr.bipi.tressence.file.FileLoggerTree
import io.github.wulkanowy.ui.base.ThemeManager
import io.github.wulkanowy.utils.ActivityLifecycleLogger
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.CrashlyticsExceptionTree
import io.github.wulkanowy.utils.CrashlyticsTree
import io.github.wulkanowy.utils.DebugLogTree
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class WulkanowyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

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
        Lingver.init(this)
        themeManager.applyDefaultTheme()

        initLogging()
    }

    private fun initLogging() {
        if (appInfo.isDebug) {
            Timber.plant(DebugLogTree())
            Timber.plant(FileLoggerTree.Builder()
                .withFileName("wulkanowy.%g.log")
                .withDirName(applicationContext.filesDir.absolutePath)
                .withFileLimit(10)
                .withMinPriority(DEBUG)
                .build()
            )
        } else {
            Timber.plant(CrashlyticsExceptionTree())
            Timber.plant(CrashlyticsTree())
        }
        registerActivityLifecycleCallbacks(ActivityLifecycleLogger())
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(if (appInfo.isDebug) VERBOSE else INFO)
        .build()
}
