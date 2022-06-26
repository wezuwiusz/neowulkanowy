package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration49 : Migration(48, 49) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS SchoolAnnouncements")

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `SchoolAnnouncements` (
            `user_login_id` INTEGER NOT NULL, 
            `date` INTEGER NOT NULL, 
            `subject` TEXT NOT NULL, 
            `content` TEXT NOT NULL, 
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            `is_notified` INTEGER NOT NULL)
        """.trimIndent()
        )
    }
}
