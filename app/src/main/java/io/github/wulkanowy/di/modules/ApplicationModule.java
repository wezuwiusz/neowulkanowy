package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.Repository;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.DbContract;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.dao.DbRepository;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.resources.ResourcesRepository;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.db.shared.SharedPrefRepository;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.data.sync.SyncRepository;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;
import io.github.wulkanowy.utils.AppConstant;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @ApplicationContext
    @Provides
    Context provideAppContext() {
        return application;
    }

    @DatabaseInfo
    @Provides
    String provideDatabaseName() {
        return AppConstant.DATABASE_NAME;
    }

    @SharedPreferencesInfo
    @Provides
    String provideSharedPreferencesName() {
        return AppConstant.SHARED_PREFERENCES_NAME;
    }

    @Singleton
    @Provides
    DaoSession provideDaoSession(DbHelper dbHelper) {
        return new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    @Singleton
    @Provides
    Vulcan provideVulcan() {
        return new Vulcan();
    }

    @Singleton
    @Provides
    RepositoryContract provideRepository(Repository repository) {
        return repository;
    }

    @Singleton
    @Provides
    SharedPrefContract provideSharedPref(SharedPrefRepository sharedPrefRepository) {
        return sharedPrefRepository;
    }

    @Singleton
    @Provides
    ResourcesContract provideAppResources(ResourcesRepository resourcesRepository) {
        return resourcesRepository;
    }


    @Singleton
    @Provides
    DbContract provideDatabase(DbRepository dbRepository) {
        return dbRepository;
    }

    @Singleton
    @Provides
    SyncContract provideSync(SyncRepository syncRepository) {
        return syncRepository;
    }

    @Provides
    FirebaseJobDispatcher provideDispatcher() {
        return new FirebaseJobDispatcher(new GooglePlayDriver(application));
    }
}
