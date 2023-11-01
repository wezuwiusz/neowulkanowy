package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration40 : Migration(39, 40) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `Notifications` (
                `student_id` INTEGER NOT NULL, 
                `title` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `type` TEXT NOT NULL, 
                `date` INTEGER NOT NULL, 
                `data` TEXT,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
            )
            """
        )
    }
}
