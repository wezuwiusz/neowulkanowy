package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration34 : Migration(33, 34) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM ReportingUnits")
        db.execSQL("DELETE FROM Recipients")
    }
}

