package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDateTime.now
import java.time.ZoneOffset

class Migration5 : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN registration_date INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("UPDATE Students SET registration_date = '${now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli()}'")
        database.execSQL("DROP TABLE IF EXISTS Notes")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Notes (
                id INTEGER PRIMARY KEY NOT NULL,
                is_read INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                date INTEGER NOT NULL,
                teacher TEXT NOT NULL,
                category TEXT NOT NULL,
                content TEXT NOT NULL)
            """)
    }
}
