package io.github.wulkanowy.data.db.dao;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;

@Singleton
public class DbHelper extends DaoMaster.DevOpenHelper {

    @Inject
    DbHelper(@ApplicationContext Context context, @DatabaseInfo String dbName) {
        super(context, dbName);
    }
}
