package io.github.wulkanowy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.greendao.query.QueryBuilder;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.utils.Log;
import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;

public class WulkanowyApp extends Application {

    public static final String DEBUG_TAG = "WulaknowyActivity";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            enableDebugLog();
        }

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "wulkanowy-db");

        daoSession = new DaoMaster(devOpenHelper.getWritableDb()).newSession();

        int schemaVersion = getSharedPreferences("LoginData", Context.MODE_PRIVATE).getInt("schemaVersion", 0);

        if (DaoMaster.SCHEMA_VERSION != schemaVersion) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("userId", 0);
            editor.putInt("schemaVersion", DaoMaster.SCHEMA_VERSION);
            editor.apply();
        }

    }

    private void enableDebugLog() {
        QueryBuilder.LOG_VALUES = true;
        FlexibleAdapter.enableLogs(Log.Level.DEBUG);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
