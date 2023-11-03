package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration44 : Migration(43, 44) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE AdminMessages ADD COLUMN is_dismissible INTEGER NOT NULL DEFAULT 0")
    }
}
