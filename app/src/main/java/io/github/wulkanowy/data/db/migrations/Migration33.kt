package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration33 : Migration(32, 33) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS StudentInfo")

        db.execSQL(
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
                first_guardian_full_name TEXT, 
                first_guardian_kinship TEXT, 
                first_guardian_address TEXT, 
                first_guardian_phones TEXT, 
                first_guardian_email TEXT, 
                second_guardian_full_name TEXT, 
                second_guardian_kinship TEXT, 
                second_guardian_address TEXT, 
                second_guardian_phones TEXT, 
                second_guardian_email TEXT)
            """
        )
    }
}

