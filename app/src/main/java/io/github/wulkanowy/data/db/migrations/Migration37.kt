package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration37 : Migration(36, 37) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TimetableHeaders (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                diary_id INTEGER NOT NULL,
                date INTEGER NOT NULL,
                content TEXT NOT NULL
            )
        """
        )
    }
}
