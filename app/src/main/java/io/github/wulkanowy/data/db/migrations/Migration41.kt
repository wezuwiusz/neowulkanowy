package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration41 : Migration(40, 41) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_added_by_user INTEGER NOT NULL DEFAULT 0")
    }
}