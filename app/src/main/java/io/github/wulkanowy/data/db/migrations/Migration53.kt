package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration53 : Migration(52, 53) {

    override fun migrate(db: SupportSQLiteDatabase) {
        createMailboxTable(db)
        recreateMessagesTable(db)
    }

    private fun createMailboxTable(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Mailboxes")
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `Mailboxes` (
                    `globalKey` TEXT NOT NULL,
                    `email` TEXT NOT NULL,
                    `symbol` TEXT NOT NULL,
                    `schoolId` TEXT NOT NULL,
                    `fullName` TEXT NOT NULL,
                    `userName` TEXT NOT NULL,
                    `studentName` TEXT NOT NULL,
                    `schoolNameShort` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    PRIMARY KEY(`globalKey`)
                )""".trimIndent()
        )
    }

    private fun recreateMessagesTable(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Messages")
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `Messages` (
                    `email` TEXT NOT NULL,
                    `message_global_key` TEXT NOT NULL,
                    `mailbox_key` TEXT NOT NULL,
                    `message_id` INTEGER NOT NULL,
                    `correspondents` TEXT NOT NULL,
                    `subject` TEXT NOT NULL,
                    `date` INTEGER NOT NULL,
                    `folder_id` INTEGER NOT NULL,
                    `unread` INTEGER NOT NULL,
                    `read_by` INTEGER,
                    `unread_by` INTEGER,
                    `has_attachments` INTEGER NOT NULL,
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `is_notified` INTEGER NOT NULL, 
                    `content` TEXT NOT NULL,
                    `sender` TEXT,
                    `recipients` TEXT
                )""".trimIndent()
        )
    }
}
