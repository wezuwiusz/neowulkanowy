package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE LuckyNumbers (" +
            "id INTEGER NOT NULL PRIMARY KEY, " +
            "is_notified INTEGER NOT NULL, " +
            "student_id INTEGER NOT NULL, " +
            "date INTEGER NOT NULL, " +
            "lucky_number INTEGER NOT NULL)")
    }
}
