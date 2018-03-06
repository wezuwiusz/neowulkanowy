package io.github.wulkanowy.data.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.utils.LogUtils;

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
        cleanUserData(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cleanUserData(new StandardDatabase(db), oldVersion, newVersion);
    }

    private void cleanUserData(Database database, int oldVersion, int newVersion) {
        LogUtils.info("Cleaning user data oldVersion=" + oldVersion + " newVersion=" + newVersion);
        DaoMaster.dropAllTables(database, true);
        onCreate(database);
        sharedPref.setCurrentUserId(0);
    }
}
