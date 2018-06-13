package io.github.wulkanowy.data;

import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.DbContract;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;

@Singleton
public interface RepositoryContract {

    SharedPrefContract getSharedRepo();

    ResourcesContract getResRepo();

    DbContract getDbRepo();

    SyncContract getSyncRepo();

    void cleanAllData();
}
