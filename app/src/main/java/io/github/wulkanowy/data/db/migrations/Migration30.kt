package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration30 : Migration(29, 30) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE TimetableAdditional (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                diary_id INTEGER NOT NULL,
                start INTEGER NOT NULL,
                `end` INTEGER NOT NULL,
                date INTEGER NOT NULL,
                subject TEXT NOT NULL
            )
        """)
    }
}
