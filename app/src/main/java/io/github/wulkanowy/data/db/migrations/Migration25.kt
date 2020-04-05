package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration25 : Migration(24, 25) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_done INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE Homework ADD COLUMN attachments TEXT NOT NULL DEFAULT \"[]\"")
    }
}
