package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration42 : Migration(41, 42) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `AdminMessages` (
            `id` INTEGER NOT NULL, 
            `title` TEXT NOT NULL, 
            `content` TEXT NOT NULL, 
            `version_name` INTEGER, 
            `version_max` INTEGER, 
            `target_register_host` TEXT, 
            `target_flavor` TEXT,
            `destination_url` TEXT,
            `priority` TEXT NOT NULL,
            `type` TEXT NOT NULL, 
            PRIMARY KEY(`id`))"""
        )
    }
}