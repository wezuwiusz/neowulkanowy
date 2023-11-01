package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration22 : Migration(21, 22) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Students ADD COLUMN school_short TEXT NOT NULL DEFAULT ''")
    }
}
