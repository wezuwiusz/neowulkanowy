package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration9 : Migration(8, 9) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Messages")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Messages (
                id INTEGER PRIMARY KEY NOT NULL,
                student_id INTEGER NOT NULL,
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                sender_name TEXT NOT NULL,
                sender_id INTEGER NOT NULL,
                recipient_name TEXT NOT NULL,
                subject TEXT NOT NULL,
                date INTEGER NOT NULL,
                folder_id INTEGER NOT NULL,
                unread INTEGER NOT NULL,
                unread_by INTEGER NOT NULL,
                read_by INTEGER NOT NULL,
                removed INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                content TEXT)
            """)
    }
}
