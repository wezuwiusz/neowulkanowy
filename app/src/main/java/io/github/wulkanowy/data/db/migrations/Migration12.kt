package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration12 : Migration(11, 12) {

    override fun migrate(database: SupportSQLiteDatabase) {
        createTempStudentsTable(database)
        replaceStudentTable(database)
        updateStudentsWithClassId(database, getStudentsIds(database))
        removeStudentsWithNoClassId(database)
        ensureThereIsOnlyOneCurrentStudent(database)
    }

    private fun createTempStudentsTable(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Students_tmp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                endpoint TEXT NOT NULL,
                loginType TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                symbol TEXT NOT NULL,
                student_id INTEGER NOT NULL,
                student_name TEXT NOT NULL,
                school_id TEXT NOT NULL,
                school_name TEXT NOT NULL,
                is_current INTEGER NOT NULL,
                registration_date INTEGER NOT NULL,
                class_id INTEGER NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX index_Students_email_symbol_student_id_school_id_class_id ON Students_tmp (email, symbol, student_id, school_id, class_id)")
    }

    private fun replaceStudentTable(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN class_id INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("INSERT INTO Students_tmp SELECT * FROM Students")
        database.execSQL("DROP TABLE Students")
        database.execSQL("ALTER TABLE Students_tmp RENAME TO Students")
    }

    private fun getStudentsIds(database: SupportSQLiteDatabase): List<Int> {
        val students = mutableListOf<Int>()
        database.query("SELECT student_id FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(it.getInt(0))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun updateStudentsWithClassId(database: SupportSQLiteDatabase, students: List<Int>) {
        students.forEach {
            database.execSQL("UPDATE Students SET class_id = IFNULL((SELECT class_id FROM Semesters WHERE student_id = $it), 0) WHERE student_id = $it")
        }
    }

    private fun removeStudentsWithNoClassId(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM Students WHERE class_id = 0")
    }

    private fun ensureThereIsOnlyOneCurrentStudent(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE Students SET is_current = 0")
        database.execSQL("UPDATE Students SET is_current = 1 WHERE id = (SELECT MAX(id) FROM Students)")
    }
}
