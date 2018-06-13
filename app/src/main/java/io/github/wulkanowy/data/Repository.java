package io.github.wulkanowy.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.DbContract;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;

@Singleton
public class Repository implements RepositoryContract {

    private final DbContract database;

    private final ResourcesContract resources;

    private final SharedPrefContract sharedPref;

    private final SyncContract synchronization;

    @Inject
    Repository(DbContract database, ResourcesContract resources, SharedPrefContract sharedPref,
               SyncContract synchronization) {
        this.database = database;
        this.resources = resources;
        this.sharedPref = sharedPref;
        this.synchronization = synchronization;
    }

    @Override
    public SharedPrefContract getSharedRepo() {
        return sharedPref;
    }

    @Override
    public ResourcesContract getResRepo() {
        return resources;
    }

    @Override
    public DbContract getDbRepo() {
        return database;
    }

    @Override
    public SyncContract getSyncRepo() {
        return synchronization;
    }

    @Override
    public void cleanAllData() {
        sharedPref.cleanSharedPref();
        database.recreateDatabase();
    }
}
