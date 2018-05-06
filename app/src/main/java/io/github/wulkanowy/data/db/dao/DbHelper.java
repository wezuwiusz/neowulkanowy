package io.github.wulkanowy.data.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.migrations.Migration23;
import io.github.wulkanowy.data.db.dao.migrations.Migration26;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class DbHelper extends DaoMaster.OpenHelper {

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    @Inject
    DbHelper(@ApplicationContext Context context, @DatabaseInfo String dbName,
             SharedPrefContract sharedPref, Vulcan vulcan) {
        super(context, dbName);
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.info("Cleaning user data oldVersion=" + oldVersion + " newVersion=" + newVersion);
        Database database = new StandardDatabase(db);
        recreateDatabase(database);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        List<Migration> migrations = getMigrations();

        // Only run migrations past the old version
        for (Migration migration : migrations) {
            if (oldVersion < migration.getVersion()) {
                try {
                    LogUtils.info("Applying migration to db schema v" + migration.getVersion() + "...");
                    migration.runMigration(db, sharedPref, vulcan);
                    LogUtils.info("Migration " + migration.getVersion() + " complete");
                } catch (Exception e) {
                    e.printStackTrace();
                    recreateDatabase(db);
                    break;
                }
            }
        }
    }

    private void recreateDatabase(Database db) {
        LogUtils.info("Database is recreating...");
        sharedPref.setCurrentUserId(0);
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new Migration23());
        migrations.add(new Migration26());

        // Sorting just to be safe, in case other people add migrations in the wrong order.
        Comparator<Migration> migrationComparator = new Comparator<Migration>() {
            @Override
            public int compare(Migration m1, Migration m2) {
                return m1.getVersion().compareTo(m2.getVersion());
            }
        };
        Collections.sort(migrations, migrationComparator);

        return migrations;
    }

    public interface Migration {
        Integer getVersion();

        void runMigration(Database db, SharedPrefContract sharedPref, Vulcan vulcan) throws Exception;
    }
}
