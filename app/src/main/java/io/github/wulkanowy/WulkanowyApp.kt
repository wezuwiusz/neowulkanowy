package io.github.wulkanowy

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.services.sync.SyncWorkerFactory
import io.github.wulkanowy.utils.CrashlyticsTree
import io.github.wulkanowy.utils.DebugLogTree
import timber.log.Timber
import javax.inject.Inject

class WulkanowyApp : DaggerApplication() {

    @Inject
    lateinit var prefRepository: PreferencesRepository

    @Inject
    lateinit var workerFactory: SyncWorkerFactory

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initializeFabric()
        if (DEBUG) enableDebugLog()
        AppCompatDelegate.setDefaultNightMode(prefRepository.currentTheme)
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())
    }

    private fun enableDebugLog() {
        Timber.plant(DebugLogTree())
        FlexibleAdapter.enableLogs(Log.Level.DEBUG)
    }

    private fun initializeFabric() {
        Fabric.with(Fabric.Builder(this).kits(
            Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled(!BuildConfig.CRASHLYTICS_ENABLED).build()).build()
        ).debuggable(BuildConfig.DEBUG).build())
        Timber.plant(CrashlyticsTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}
