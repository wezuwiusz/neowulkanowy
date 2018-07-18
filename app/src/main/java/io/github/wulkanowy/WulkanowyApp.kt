package io.github.wulkanowy

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.fabric.sdk.android.Fabric
import io.github.wulkanowy.data.RepositoryContract
import io.github.wulkanowy.di.DaggerAppComponent
import io.github.wulkanowy.utils.FabricUtils
import io.github.wulkanowy.utils.LoggerUtils
import io.github.wulkanowy.utils.security.ScramblerException
import org.greenrobot.greendao.query.QueryBuilder
import timber.log.Timber
import javax.inject.Inject

class WulkanowyApp : DaggerApplication() {

    @Inject
    internal lateinit var repository: RepositoryContract

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            enableDebugLog()
        }
        initializeFabric()
        initializeUserSession()
    }

    private fun initializeUserSession() {
        if (repository.sharedRepo.isUserLoggedIn) {
            try {
                repository.syncRepo.initLastUser()
                FabricUtils.logLogin("Open app", true)
            } catch (e: Exception) {
                FabricUtils.logLogin("Open app", false)
                Timber.e(e, "An error occurred when the application was started")
            } catch (e: ScramblerException) {
                FabricUtils.logLogin("Open app", false)
                Timber.e(e, "A security error has occurred")
                repository.cleanAllData()
            }

        }
    }

    private fun enableDebugLog() {
        QueryBuilder.LOG_VALUES = true
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
