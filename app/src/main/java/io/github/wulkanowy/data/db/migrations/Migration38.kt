package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration38 : Migration(37, 38) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `SchoolAnnouncements` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `student_id` INTEGER NOT NULL,
                `date` INTEGER NOT NULL,
                `subject` TEXT NOT NULL,
                `content` TEXT NOT NULL
            )
        """)
    }
}
