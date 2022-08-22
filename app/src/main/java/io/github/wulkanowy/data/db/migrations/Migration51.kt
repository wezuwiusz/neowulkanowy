package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration51 : Migration(50, 51) {

    override fun migrate(database: SupportSQLiteDatabase) {
        createMailboxTable(database)
        recreateMessagesTable(database)
        recreateMessageAttachmentsTable(database)
        recreateRecipientsTable(database)
        deleteReportingUnitTable(database)
    }

    private fun createMailboxTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS Mailboxes")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `Mailboxes` (
                `globalKey` TEXT NOT NULL,
                `fullName` TEXT NOT NULL,
                `userName` TEXT NOT NULL,
                `userLoginId` INTEGER NOT NULL,
                `studentName` TEXT NOT NULL,
                `schoolNameShort` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                PRIMARY KEY(`globalKey`)
            )""".trimIndent()
        )
    }

    private fun recreateMessagesTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS Messages")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `Messages` (
                `message_global_key` TEXT NOT NULL,
                `mailbox_key` TEXT NOT NULL,
                `message_id` INTEGER NOT NULL,
                `correspondents` TEXT NOT NULL,
                `subject` TEXT NOT NULL,
                `date` INTEGER NOT NULL,
                `folder_id` INTEGER NOT NULL,
                `unread` INTEGER NOT NULL,
                `has_attachments` INTEGER NOT NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `is_notified` INTEGER NOT NULL,
                `content` TEXT NOT NULL,
                `sender` TEXT, `recipients` TEXT
            )""".trimIndent()
        )
    }

    private fun recreateMessageAttachmentsTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS MessageAttachments")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `MessageAttachments` (
                `real_id` INTEGER NOT NULL,
                `message_global_key` TEXT NOT NULL,
                `url` TEXT NOT NULL,
                `filename` TEXT NOT NULL,
                PRIMARY KEY(`real_id`)
            )""".trimIndent()
        )
    }

    private fun recreateRecipientsTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS Recipients")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `Recipients` (
                `mailboxGlobalKey` TEXT NOT NULL,
                `studentMailboxGlobalKey` TEXT NOT NULL,
                `fullName` TEXT NOT NULL,
                `userName` TEXT NOT NULL,
                `schoolShortName` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
            )""".trimIndent()
        )
    }

    private fun deleteReportingUnitTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS ReportingUnits")
    }
}
