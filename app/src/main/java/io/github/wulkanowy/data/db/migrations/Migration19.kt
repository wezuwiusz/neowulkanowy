package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider

class Migration19(private val sharedPrefProvider: SharedPrefProvider) : Migration(18, 19) {

    override fun migrate(db: SupportSQLiteDatabase) {
        migrateMessages(db)
        migrateGrades(db)
        migrateStudents(db)
        migrateSharedPreferences()
    }

    private fun migrateMessages(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE Messages")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                is_notified INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                sender_name TEXT NOT NULL,
                sender_id INTEGER NOT NULL,
                recipient_name TEXT NOT NULL,
                subject TEXT NOT NULL,
                content TEXT NOT NULL,
                date INTEGER NOT NULL,
                folder_id INTEGER NOT NULL,
                unread INTEGER NOT NULL,
                unread_by INTEGER NOT NULL,
                read_by INTEGER NOT NULL,
                removed INTEGER NOT NULL
            )
        """
        )
    }

    private fun migrateGrades(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE Grades")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                is_read INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                entry TEXT NOT NULL,
                value REAL NOT NULL,
                modifier REAL NOT NULL,
                comment TEXT NOT NULL,
                color TEXT NOT NULL,
                grade_symbol TEXT NOT NULL,
                description TEXT NOT NULL,
                weight TEXT NOT NULL,
                weightValue REAL NOT NULL,
                date INTEGER NOT NULL,
                teacher TEXT NOT NULL
            )
        """
        )
    }

    private fun migrateStudents(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Students_tmp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scrapper_base_url TEXT NOT NULL,
                mobile_base_url TEXT NOT NULL,
                is_parent INTEGER NOT NULL,
                login_type TEXT NOT NULL,
                login_mode TEXT NOT NULL,
                certificate_key TEXT NOT NULL,
                private_key TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                symbol TEXT NOT NULL,
                student_id INTEGER NOT NULL,
                user_login_id INTEGER NOT NULL,
                student_name TEXT NOT NULL,
                school_id TEXT NOT NULL,
                school_name TEXT NOT NULL,
                class_name TEXT NOT NULL,
                class_id INTEGER NOT NULL,
                is_current INTEGER NOT NULL,
                registration_date INTEGER NOT NULL
            )
        """
        )

        db.execSQL("ALTER TABLE Students ADD COLUMN scrapperBaseUrl TEXT NOT NULL DEFAULT \"\";")
        db.execSQL("ALTER TABLE Students ADD COLUMN apiBaseUrl TEXT NOT NULL DEFAULT \"\";")
        db.execSQL("ALTER TABLE Students ADD COLUMN is_parent INT NOT NULL DEFAULT 0;")
        db.execSQL("ALTER TABLE Students ADD COLUMN loginMode TEXT NOT NULL DEFAULT \"\";")
        db.execSQL("ALTER TABLE Students ADD COLUMN certificateKey TEXT NOT NULL DEFAULT \"\";")
        db.execSQL("ALTER TABLE Students ADD COLUMN privateKey TEXT NOT NULL DEFAULT \"\";")
        db.execSQL("ALTER TABLE Students ADD COLUMN user_login_id INTEGER NOT NULL DEFAULT 0;")

        db.execSQL(
            """
            INSERT INTO Students_tmp(
            id, scrapper_base_url, mobile_base_url, is_parent, login_type, login_mode, certificate_key, private_key, email, password, symbol, student_id, user_login_id, student_name, school_id, school_name, school_id, school_name, class_name, class_id, is_current, registration_date)
            SELECT
            id, endpoint, apiBaseUrl, is_parent, loginType, "SCRAPPER", certificateKey, privateKey, email, password, symbol, student_id, user_login_id, student_name, school_id, school_name, school_id, school_name, class_name, class_id, is_current, registration_date
            FROM Students
        """
        )
        db.execSQL("DROP TABLE Students")
        db.execSQL("ALTER TABLE Students_tmp RENAME TO Students")
        db.execSQL("CREATE UNIQUE INDEX index_Students_email_symbol_student_id_school_id_class_id ON Students (email, symbol, student_id, school_id, class_id)")
    }

    private fun migrateSharedPreferences() {
        if (sharedPrefProvider.getString("grade_modifier_plus", "0.0") == "0.0") {
            sharedPrefProvider.putString("grade_modifier_plus", "0.33")
        }
        if (sharedPrefProvider.getString("grade_modifier_minus", "0.0") == "0.0") {
            sharedPrefProvider.putString("grade_modifier_minus", "0.33")
        }
    }
}
