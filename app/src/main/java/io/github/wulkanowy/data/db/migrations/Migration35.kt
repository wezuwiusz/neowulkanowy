package io.github.wulkanowy.data.db.migrations

import androidx.core.database.getLongOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.utils.AppInfo

class Migration35(private val appInfo: AppInfo) : Migration(34, 35) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Students ADD COLUMN `avatar_color` INTEGER NOT NULL DEFAULT 0")

        db.query("SELECT * FROM Students").use {
            while (it.moveToNext()) {
                val studentId = it.getLongOrNull(0)
                db.execSQL(
                    """
                        UPDATE Students
                        SET avatar_color = ${appInfo.defaultColorsForAvatar.random()}
                        WHERE id = $studentId
                    """
                )
            }
        }
    }
}
