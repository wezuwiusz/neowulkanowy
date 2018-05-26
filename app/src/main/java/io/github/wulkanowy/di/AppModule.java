package io.github.wulkanowy.di;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.WulkanowyApp;
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
import io.github.wulkanowy.utils.AppConstant;

@Module
public abstract class AppModule {

    @Binds
    abstract Context provideContext(WulkanowyApp app);

    @Singleton
    @Binds
    abstract RepositoryContract provideRepository(Repository repository);

    @Singleton
    @Binds
    abstract DbContract provideDatabse(DbRepository dbRepository);

    @Singleton
    @Binds
    abstract SharedPrefContract provideSharedPref(SharedPrefRepository sharedPrefRepository);

    @Singleton
    @Binds
    abstract SyncContract provideSync(SyncRepository syncRepository);

    @Singleton
    @Binds
    abstract ResourcesContract provideResources(ResourcesRepository resourcesRepository);

    @Singleton
    @Provides
    static DaoSession provideDaoSession(DbHelper dbHelper) {
        return new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    @Singleton
    @Provides
    static Vulcan provideVulcan() {
        return new Vulcan();
    }

    @Provides
    @Named("dbName")
    static String provideDbName() {
        return AppConstant.DATABASE_NAME;
    }

    @Provides
    @Named("sharedPrefName")
    static String provideSharedPrefName() {
        return AppConstant.SHARED_PREFERENCES_NAME;
    }

}
