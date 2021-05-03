package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration36 : Migration(35, 36) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Exams ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_notified INTEGER NOT NULL DEFAULT 1")
    }
}
