package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration7 : Migration(6, 7) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GradesStatistics (
                id INTEGER PRIMARY KEY NOT NULL,
                student_id INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                grade INTEGER NOT NULL,
                amount INTEGER NOT NULL,
                is_semester INTEGER NOT NULL)
            """
        )
    }
}
