package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE CompletedLesson (" +
            "id INTEGER NOT NULL PRIMARY KEY, " +
            "student_id INTEGER NOT NULL, " +
            "diary_id INTEGER NOT NULL, " +
            "date INTEGER NOT NULL, " +
            "number INTEGER NOT NULL, " +
            "subject TEXT NOT NULL, " +
            "topic TEXT NOT NULL, " +
            "teacher TEXT NOT NULL, " +
            "teacher_symbol TEXT NOT NULL, " +
            "substitution TEXT NOT NULL, " +
            "absence TEXT NOT NULL, " +
            "resources TEXT NOT NULL)")
    }
}
