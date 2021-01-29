package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration31 : Migration(30, 31) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS StudentInfo (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                student_id INTEGER NOT NULL, 
                full_name TEXT NOT NULL, 
                first_name TEXT NOT NULL, 
                second_name TEXT NOT NULL, 
                surname TEXT NOT NULL, 
                birth_date INTEGER NOT NULL, 
                birth_place TEXT NOT NULL, 
                gender TEXT NOT NULL, 
                has_polish_citizenship INTEGER NOT NULL, 
                family_name TEXT NOT NULL, 
                parents_names TEXT NOT NULL, 
                address TEXT NOT NULL, 
                registered_address TEXT NOT NULL, 
                correspondence_address TEXT NOT NULL, 
                phone_number TEXT NOT NULL, 
                cell_phone_number TEXT NOT NULL,
                email TEXT NOT NULL, 
                first_guardian_full_name TEXT NOT NULL, 
                first_guardian_kinship TEXT NOT NULL, 
                first_guardian_address TEXT NOT NULL, 
                first_guardian_phones TEXT NOT NULL, 
                first_guardian_email TEXT NOT NULL, 
                second_guardian_full_name TEXT NOT NULL, 
                second_guardian_kinship TEXT NOT NULL, 
                second_guardian_address TEXT NOT NULL, 
                second_guardian_phones TEXT NOT NULL, 
                second_guardian_email TEXT NOT NULL)
            """
        )
    }
}
