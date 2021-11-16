package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration43 : Migration(42, 43) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Timetable ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE Attendance ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}
