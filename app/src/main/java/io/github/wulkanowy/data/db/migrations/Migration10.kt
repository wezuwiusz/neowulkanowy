package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration10 : Migration(9, 10) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Grades_Summary RENAME TO GradesSummary")
    }
}
