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
import javax.inject.Named;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.migrations.Migration23;
import io.github.wulkanowy.data.db.dao.migrations.Migration26;
import io.github.wulkanowy.data.db.dao.migrations.Migration27;
import io.github.wulkanowy.data.db.dao.migrations.Migration28;
import io.github.wulkanowy.data.db.dao.migrations.Migration29;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import timber.log.Timber;

@Singleton
public class DbHelper extends DaoMaster.OpenHelper {

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    @Inject
    DbHelper(Context context, @Named("dbName") String dbName,
             SharedPrefContract sharedPref, Vulcan vulcan) {
        super(context, dbName);
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("Cleaning user data oldVersion=%s newVersion=%s", oldVersion, newVersion);
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
                    Timber.i("Applying migration to db schema v%s...", migration.getVersion());
                    migration.runMigration(db, sharedPref, vulcan);
                    Timber.i("Migration %s complete", migration.getVersion());
                } catch (Exception e) {
                    Timber.e(e, "Failed to apply migration");
                    recreateDatabase(db);
                    break;
                }
            }
        }
    }

    private void recreateDatabase(Database db) {
        Timber.i("Database is recreating...");
        sharedPref.setCurrentUserId(0);
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new Migration23());
        migrations.add(new Migration26());
        migrations.add(new Migration27());
        migrations.add(new Migration28());
        migrations.add(new Migration29());

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
