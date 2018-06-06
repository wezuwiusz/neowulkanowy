package io.github.wulkanowy;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.fabric.sdk.android.Fabric;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.di.DaggerAppComponent;
import io.github.wulkanowy.utils.FabricUtils;
import io.github.wulkanowy.utils.LoggerUtils;
import timber.log.Timber;

public class WulkanowyApp extends DaggerApplication {

    @Inject
    RepositoryContract repository;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG) {
            enableDebugLog();
        }
        initializeFabric();
        initializeUserSession();
    }

    private void initializeUserSession() {
        if (repository.getSharedRepo().isUserLoggedIn()) {
            try {
                repository.getSyncRepo().initLastUser();
                FabricUtils.logLogin("Open app", true);
            } catch (Exception e) {
                FabricUtils.logLogin("Open app", false);
                Timber.e(e, "An error occurred when the application was started");
            }
        }
    }

    private void enableDebugLog() {
        QueryBuilder.LOG_VALUES = true;
        FlexibleAdapter.enableLogs(eu.davidea.flexibleadapter.utils.Log.Level.DEBUG);
        Timber.plant(new LoggerUtils.DebugLogTree());
    }

    private void initializeFabric() {
        Fabric.with(new Fabric.Builder(this)
                .kits(
                        new Crashlytics.Builder()
                                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                                .build(),
                        new Answers()
                )
                .debuggable(BuildConfig.DEBUG)
                .build());
        Timber.plant(new LoggerUtils.CrashlyticsTree());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
