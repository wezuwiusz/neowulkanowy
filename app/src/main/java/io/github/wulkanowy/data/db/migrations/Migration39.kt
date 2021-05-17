package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration39 : Migration(38, 39) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Conferences ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE SchoolAnnouncements ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}