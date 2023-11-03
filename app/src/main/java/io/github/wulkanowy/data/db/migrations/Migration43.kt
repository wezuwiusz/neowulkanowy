package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration43 : Migration(42, 43) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Timetable ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE Attendance ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}
