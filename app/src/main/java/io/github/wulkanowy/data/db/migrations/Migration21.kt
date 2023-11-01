package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration21 : Migration(20, 21) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Attendance ADD COLUMN excusable INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE Attendance ADD COLUMN time_id INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE Attendance ADD COLUMN excuse_status TEXT DEFAULT NULL")

        db.execSQL("DELETE FROM Semesters")
    }
}
