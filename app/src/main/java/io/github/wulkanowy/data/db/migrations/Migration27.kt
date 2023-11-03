package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration27 : Migration(26, 27) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Students ADD COLUMN user_name TEXT NOT NULL DEFAULT \"\"")

        val students = getStudentsIdsAndNames(db)
        val units = getReportingUnits(db)

        students.forEach { (id, userLoginId, studentName) ->
            val userNameFromUnits =
                units.singleOrNull { (senderId, _) -> senderId == userLoginId }?.second
            val normalizedStudentName = studentName.split(" ").asReversed().joinToString(" ")

            val userName = userNameFromUnits ?: normalizedStudentName
            db.execSQL("UPDATE Students SET user_name = '$userName' WHERE id = '$id'")
        }
    }

    private fun getStudentsIdsAndNames(db: SupportSQLiteDatabase): MutableList<Triple<Long, Int, String>> {
        val students = mutableListOf<Triple<Long, Int, String>>()
        db.query("SELECT id, user_login_id, student_name FROM Students").use {
            if (it.moveToFirst()) {
                do {
                    students.add(Triple(it.getLong(0), it.getInt(1), it.getString(2)))
                } while (it.moveToNext())
            }
        }

        return students
    }

    private fun getReportingUnits(db: SupportSQLiteDatabase): MutableList<Pair<Int, String>> {
        val units = mutableListOf<Pair<Int, String>>()
        db.query("SELECT sender_id, sender_name FROM ReportingUnits").use {
            if (it.moveToFirst()) {
                do {
                    units.add(it.getInt(0) to it.getString(1))
                } while (it.moveToNext())
            }
        }


        return units
    }
}
