package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration23 : Migration(22, 23) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Notes ADD COLUMN teacher_symbol TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE Notes ADD COLUMN category_type INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE Notes ADD COLUMN is_points_show INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE Notes ADD COLUMN points INTEGER NOT NULL DEFAULT 0")
    }
}
