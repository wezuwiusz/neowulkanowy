package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration54 : Migration(53, 54) {

    override fun migrate(database: SupportSQLiteDatabase) {
        migrateResman(database)
        removeTomaszowMazowieckiStudents(database)
    }

    private fun migrateResman(database: SupportSQLiteDatabase) {
        database.execSQL("""
            UPDATE Students SET
                scrapper_base_url = 'https://vulcan.net.pl',
                login_type = 'ADFSLightScoped',
                symbol = 'rzeszowprojekt'
            WHERE scrapper_base_url = 'https://resman.pl'
        """.trimIndent())
    }

    private fun removeTomaszowMazowieckiStudents(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM Students WHERE symbol = 'tomaszowmazowiecki'")
    }
}
