package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18 : Migration(17, 18) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS School (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                class_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                contact TEXT NOT NULL,
                headmaster TEXT NOT NULL,
                pedagogue TEXT NOT NULL
            )
        """)
    }
}
