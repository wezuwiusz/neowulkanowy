package io.github.wulkanowy.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;


public class WulkanowyApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "wulkanowy-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();

        int schemaVersion = getSharedPreferences("LoginData", Context.MODE_PRIVATE).getInt("schemaVersion", 0);

        if (DaoMaster.SCHEMA_VERSION != schemaVersion) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("userId", 0);
            editor.putInt("schemaVersion", DaoMaster.SCHEMA_VERSION);
            editor.apply();
        }

        QueryBuilder.LOG_VALUES = true;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
