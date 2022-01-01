package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration13 : Migration(12, 13) {

    override fun migrate(database: SupportSQLiteDatabase) {
        addClassNameToStudents(database, getStudentsIds(database))
        updateSemestersTable(database)
        markAtLeastAndOnlyOneSemesterAtCurrent(database, getStudentsAndClassIds(database))
        clearMessagesTable(database)
    }

    private fun addClassNameToStudents(database: SupportSQLiteDatabase, students: List<Pair<Int, String>>) {
        database.execSQL("ALTER TABLE Students ADD COLUMN class_name TEXT DEFAULT \"\" NOT NULL")

        students.forEach { (id, name) ->
            val schoolName = name.substringAfter(" - ")
            val className = name.substringBefore(" - ", "").replace("Klasa ", "")
            database.execSQL("UPDATE Students SET class_name = '$className' WHERE id = '$id'")
            database.execSQL("UPDATE Students SET school_name = '$schoolName' WHERE id = '$id'")
        }
    }

    private fun getStudentsIds(database: SupportSQLiteDatabase): MutableList<Pair<Int, String>> {
        val students = mutableListOf<Pair<Int, String>>()
        database.query("SELECT id, school_name FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(it.getInt(0) to it.getString(1))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun updateSemestersTable(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Semesters ADD COLUMN school_year INTEGER DEFAULT 1970 NOT NULL")
        database.execSQL("ALTER TABLE Semesters ADD COLUMN start INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE Semesters ADD COLUMN `end` INTEGER DEFAULT 0 NOT NULL")
    }

    private fun getStudentsAndClassIds(database: SupportSQLiteDatabase): List<Pair<Int, Int>> {
        val students = mutableListOf<Pair<Int, Int>>()
        database.query("SELECT student_id, class_id FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(it.getInt(0) to it.getInt(1))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun markAtLeastAndOnlyOneSemesterAtCurrent(database: SupportSQLiteDatabase, students: List<Pair<Int, Int>>) {
        students.forEach { (studentId, classId) ->
            database.execSQL("UPDATE Semesters SET is_current = 0 WHERE student_id = '$studentId' AND class_id = '$classId'")
            database.execSQL("UPDATE Semesters SET is_current = 1 WHERE id = (SELECT id FROM Semesters WHERE student_id = '$studentId' AND class_id = '$classId' ORDER BY semester_id DESC)")
        }
    }

    private fun clearMessagesTable(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM Messages")
    }
}
