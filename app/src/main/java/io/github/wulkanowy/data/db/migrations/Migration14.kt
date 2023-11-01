package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration14 : Migration(13, 14) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS GradesSummary")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GradesSummary (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                semester_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                position INTEGER NOT NULL,
                subject TEXT NOT NULL,
                predicted_grade TEXT NOT NULL,
                final_grade TEXT NOT NULL,
                proposed_points TEXT NOT NULL,
                final_points TEXT NOT NULL,
                points_sum TEXT NOT NULL,
                average REAL NOT NULL
            )
        """)
    }
}
