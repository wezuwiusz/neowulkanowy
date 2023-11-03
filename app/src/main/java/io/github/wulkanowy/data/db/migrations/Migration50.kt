package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration50 : Migration(49, 50) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS MobileDevices")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `MobileDevices` (
            `user_login_id` INTEGER NOT NULL, 
            `device_id` INTEGER NOT NULL, 
            `name` TEXT NOT NULL, 
            `date` INTEGER NOT NULL, 
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)
        """.trimIndent()
        )
    }
}
