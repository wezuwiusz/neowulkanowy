package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration6 : Migration(5, 6) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS ReportingUnits (
                id INTEGER PRIMARY KEY NOT NULL,
                student_id INTEGER NOT NULL,
                real_id INTEGER NOT NULL,
                short TEXT NOT NULL,
                sender_id INTEGER NOT NULL,
                sender_name TEXT NOT NULL,
                roles TEXT NOT NULL)
                """)

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Recipients (
                id INTEGER PRIMARY KEY NOT NULL,
                student_id INTEGER NOT NULL,
                real_id TEXT NOT NULL,
                name TEXT NOT NULL,
                real_name TEXT NOT NULL,
                login_id INTEGER NOT NULL,
                unit_id INTEGER NOT NULL,
                role INTEGER NOT NULL,
                hash TEXT NOT NULL)
                """)

        database.execSQL("DELETE FROM Semesters WHERE 1")
        database.execSQL("ALTER TABLE Semesters ADD COLUMN class_id INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE Semesters ADD COLUMN unit_id INTEGER DEFAULT 0 NOT NULL")
    }
}
