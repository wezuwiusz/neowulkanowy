package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration63 : AutoMigrationSpec {

    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE Students SET is_edu_one = NULL WHERE is_edu_one = 0")
    }
}
