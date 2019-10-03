package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration17 : Migration(16, 17) {

    override fun migrate(database: SupportSQLiteDatabase) {
        createGradesPointsStatisticsTable(database)
        truncateSemestersTable(database)
    }

    private fun createGradesPointsStatisticsTable(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS GradesPointsStatistics(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                others REAL NOT NULL,
                student REAL NOT NULL
            )
        """)
    }

    private fun truncateSemestersTable(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM Semesters")
    }
}
