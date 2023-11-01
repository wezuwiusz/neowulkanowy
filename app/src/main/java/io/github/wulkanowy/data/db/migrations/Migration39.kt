package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration39 : Migration(38, 39) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Conferences ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE SchoolAnnouncements ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}
