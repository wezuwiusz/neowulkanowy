package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration24 : Migration(23, 24) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Messages ADD COLUMN has_attachments INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS MessageAttachments (
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                one_drive_id TEXT NOT NULL,
                url TEXT NOT NULL,
                filename TEXT NOT NULL,
                PRIMARY KEY(real_id)
            )
        """
        )
    }
}
