package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration8 : Migration(7, 8) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Timetable ADD COLUMN subjectOld TEXT DEFAULT \"\" NOT NULL")
        db.execSQL("ALTER TABLE Timetable ADD COLUMN roomOld TEXT DEFAULT \"\" NOT NULL")
        db.execSQL("ALTER TABLE Timetable ADD COLUMN teacherOld TEXT DEFAULT \"\" NOT NULL")
    }
}
