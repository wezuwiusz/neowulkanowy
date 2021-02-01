package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration32 : Migration(31, 32) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN nick TEXT NOT NULL DEFAULT \"\"")
    }
}

