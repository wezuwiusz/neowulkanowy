package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2 : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS LuckyNumbers (
                id INTEGER PRIMARY KEY NOT NULL,
                is_notified INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                date INTEGER NOT NULL,
                lucky_number INTEGER NOT NULL)
            """
        )
    }
}
