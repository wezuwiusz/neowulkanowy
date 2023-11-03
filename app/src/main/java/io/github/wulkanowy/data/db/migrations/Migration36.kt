package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration36 : Migration(35, 36) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Exams ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE Homework ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}
