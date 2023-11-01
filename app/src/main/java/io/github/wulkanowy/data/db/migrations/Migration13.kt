package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration13 : Migration(12, 13) {

    override fun migrate(db: SupportSQLiteDatabase) {
        addClassNameToStudents(db, getStudentsIds(db))
        updateSemestersTable(db)
        markAtLeastAndOnlyOneSemesterAtCurrent(db, getStudentsAndClassIds(db))
        clearMessagesTable(db)
    }

    private fun addClassNameToStudents(
        db: SupportSQLiteDatabase,
        students: List<Pair<Int, String>>
    ) {
        db.execSQL("ALTER TABLE Students ADD COLUMN class_name TEXT DEFAULT \"\" NOT NULL")

        students.forEach { (id, name) ->
            val schoolName = name.substringAfter(" - ")
            val className = name.substringBefore(" - ", "").replace("Klasa ", "")
            db.execSQL("UPDATE Students SET class_name = '$className' WHERE id = '$id'")
            db.execSQL("UPDATE Students SET school_name = '$schoolName' WHERE id = '$id'")
        }
    }

    private fun getStudentsIds(db: SupportSQLiteDatabase): MutableList<Pair<Int, String>> {
        val students = mutableListOf<Pair<Int, String>>()
        db.query("SELECT id, school_name FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(it.getInt(0) to it.getString(1))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun updateSemestersTable(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Semesters ADD COLUMN school_year INTEGER DEFAULT 1970 NOT NULL")
        db.execSQL("ALTER TABLE Semesters ADD COLUMN start INTEGER DEFAULT 0 NOT NULL")
        db.execSQL("ALTER TABLE Semesters ADD COLUMN `end` INTEGER DEFAULT 0 NOT NULL")
    }

    private fun getStudentsAndClassIds(db: SupportSQLiteDatabase): List<Pair<Int, Int>> {
        val students = mutableListOf<Pair<Int, Int>>()
        db.query("SELECT student_id, class_id FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(it.getInt(0) to it.getInt(1))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun markAtLeastAndOnlyOneSemesterAtCurrent(
        db: SupportSQLiteDatabase,
        students: List<Pair<Int, Int>>
    ) {
        students.forEach { (studentId, classId) ->
            db.execSQL("UPDATE Semesters SET is_current = 0 WHERE student_id = '$studentId' AND class_id = '$classId'")
            db.execSQL("UPDATE Semesters SET is_current = 1 WHERE id = (SELECT id FROM Semesters WHERE student_id = '$studentId' AND class_id = '$classId' ORDER BY semester_id DESC)")
        }
    }

    private fun clearMessagesTable(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM Messages")
    }
}
