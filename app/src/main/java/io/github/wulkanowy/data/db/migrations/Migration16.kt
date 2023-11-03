package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration16 : Migration(15, 16) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Teachers (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                class_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                name TEXT NOT NULL,
                short_name TEXT NOT NULL
            )
        """
        )
    }
}
