package io.github.wulkanowy;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.utils.Log;
import io.fabric.sdk.android.Fabric;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.di.component.ApplicationComponent;
import io.github.wulkanowy.di.component.DaggerApplicationComponent;
import io.github.wulkanowy.di.modules.ApplicationModule;

public class WulkanowyApp extends Application {

    protected ApplicationComponent applicationComponent;

    @Inject
    RepositoryContract repository;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);

        initializeFabric();
        if (BuildConfig.DEBUG) {
            enableDebugLog();
        }

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    private void enableDebugLog() {
        QueryBuilder.LOG_VALUES = true;
        FlexibleAdapter.enableLogs(Log.Level.DEBUG);
    }

    private void initializeFabric() {
        Fabric.with(new Fabric.Builder(this)
                .kits(new Crashlytics.Builder()
                        .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build())
                .debuggable(BuildConfig.DEBUG)
                .build());
    }

    public DaoSession getDaoSession() {
        return null;
    }
}
