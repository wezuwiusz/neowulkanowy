package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration11 : Migration(10, 11) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Grades_temp (
                id INTEGER PRIMARY KEY NOT NULL,
                is_read INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                entry TEXT NOT NULL,
                value INTEGER NOT NULL,
                modifier REAL NOT NULL,
                comment TEXT NOT NULL,
                color TEXT NOT NULL,
                grade_symbol TEXT NOT NULL,
                description TEXT NOT NULL,
                weight TEXT NOT NULL,
                weightValue REAL NOT NULL,
                date INTEGER NOT NULL,
                teacher TEXT NOT NULL
            )
        """
        )
        db.execSQL("INSERT INTO Grades_temp SELECT * FROM Grades")
        db.execSQL("DROP TABLE Grades")
        db.execSQL("ALTER TABLE Grades_temp RENAME TO Grades")
    }
}
