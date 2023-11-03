package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration29 : Migration(28, 29) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS GradesStatistics")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GradeSemesterStatistics (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                amounts TEXT NOT NULL,
                student_grade INTEGER NOT NULL
            )
        """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GradePartialStatistics (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                class_average TEXT NOT NULL,
                student_average TEXT NOT NULL,
                class_amounts TEXT NOT NULL,
                student_amounts TEXT NOT NULL
            )
        """)
    }
}
