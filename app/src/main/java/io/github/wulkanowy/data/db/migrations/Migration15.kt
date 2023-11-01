package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration15 : Migration(14, 15) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS MobileDevices (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                device_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                date INTEGER NOT NULL
            )
        """
        )
    }
}
