package io.github.wulkanowy.data.db.migrations

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.utils.AppInfo
import org.junit.Rule

abstract class AbstractMigrationTest {

    val dbName = "migration-test"

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        listOf(Migration55()),
        FrameworkSQLiteOpenHelperFactory()
    )

    fun runMigrationsAndValidate(migration: Migration) {
        helper.runMigrationsAndValidate(dbName, migration.endVersion, true, migration).close()
    }

    fun getMigratedRoomDatabase(): AppDatabase {
        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            dbName
        ).addMigrations(
            *AppDatabase.getMigrations(
                SharedPrefProvider(PreferenceManager.getDefaultSharedPreferences(context)),
                AppInfo()
            )
        ).build()
        // close the database and release any stream resources when the test finishes
        helper.closeWhenFinished(database)
        return database
    }
}
