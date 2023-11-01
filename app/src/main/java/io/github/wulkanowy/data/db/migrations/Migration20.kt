package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration20 : Migration(19, 20) {

    override fun migrate(db: SupportSQLiteDatabase) {
        migrateTimetable(db)
        truncateSubjects(db)
    }

    private fun migrateTimetable(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE Timetable")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `Timetable` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `student_id` INTEGER NOT NULL,
                `diary_id` INTEGER NOT NULL,
                `number` INTEGER NOT NULL,
                `start` INTEGER NOT NULL,
                `end` INTEGER NOT NULL,
                `date` INTEGER NOT NULL,
                `subject` TEXT NOT NULL,
                `subjectOld` TEXT NOT NULL,
                `group` TEXT NOT NULL,
                `room` TEXT NOT NULL,
                `roomOld` TEXT NOT NULL,
                `teacher` TEXT NOT NULL,
                `teacherOld` TEXT NOT NULL,
                `info` TEXT NOT NULL,
                `student_plan` INTEGER NOT NULL,
                `changes` INTEGER NOT NULL,
                `canceled` INTEGER NOT NULL
            )
        """
        )
    }

    private fun truncateSubjects(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM Subjects")
    }
}
