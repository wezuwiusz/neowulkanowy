package io.github.wulkanowy.data.db.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

@DeleteColumn(
    tableName = "MessageAttachments",
    columnName = "real_id",
)
class Migration55 : AutoMigrationSpec {

    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM Messages")
        db.execSQL("DELETE FROM MessageAttachments")
    }
}
