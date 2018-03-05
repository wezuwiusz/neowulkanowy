package io.github.wulkanowy.data.db.dao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;

@Singleton
public class DbHelper extends DaoMaster.DevOpenHelper {

    private SharedPrefContract sharedPref;

    @Inject
    DbHelper(@ApplicationContext Context context, @DatabaseInfo String dbName,
             SharedPrefContract sharedPref) {
        super(context, dbName);
        this.sharedPref = sharedPref;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        sharedPref.setCurrentUserId(0);
    }
}
