package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration54 : Migration(53, 54) {

    override fun migrate(db: SupportSQLiteDatabase) {
        migrateResman(db)
        removeTomaszowMazowieckiStudents(db)
    }

    private fun migrateResman(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            UPDATE Students SET
                scrapper_base_url = 'https://vulcan.net.pl',
                login_type = 'ADFSLightScoped',
                symbol = 'rzeszowprojekt'
            WHERE scrapper_base_url = 'https://resman.pl'
        """.trimIndent()
        )
    }

    private fun removeTomaszowMazowieckiStudents(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM Students WHERE symbol = 'tomaszowmazowiecki'")
    }
}
