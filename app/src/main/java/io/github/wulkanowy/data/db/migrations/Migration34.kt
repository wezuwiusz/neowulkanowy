package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration34 : Migration(33, 34) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM ReportingUnits")
        database.execSQL("DELETE FROM Recipients")
    }
}

