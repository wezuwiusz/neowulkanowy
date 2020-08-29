package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration27 : Migration(26, 27) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN user_name TEXT NOT NULL DEFAULT \"\"")

        val students = getStudentsIdsAndNames(database)
        val units = getReportingUnits(database)

        students.forEach { (id, userLoginId, studentName) ->
            val userNameFromUnits = units.singleOrNull { (senderId, _) -> senderId == userLoginId }?.second
            val normalizedStudentName = studentName.split(" ").asReversed().joinToString(" ")

            val userName = userNameFromUnits ?: normalizedStudentName
            database.execSQL("UPDATE Students SET user_name = '$userName' WHERE id = '$id'")
        }
    }

    private fun getStudentsIdsAndNames(database: SupportSQLiteDatabase): MutableList<Triple<Long, Int, String>> {
        val students = mutableListOf<Triple<Long, Int, String>>()
        val studentsCursor = database.query("SELECT id, user_login_id, student_name FROM Students")
        if (studentsCursor.moveToFirst()) {
            do {
                students.add(Triple(studentsCursor.getLong(0), studentsCursor.getInt(1), studentsCursor.getString(2)))
            } while (studentsCursor.moveToNext())
        }
        return students
    }

    private fun getReportingUnits(database: SupportSQLiteDatabase): MutableList<Pair<Int, String>> {
        val units = mutableListOf<Pair<Int, String>>()
        val unitsCursor = database.query("SELECT sender_id, sender_name FROM ReportingUnits")
        if (unitsCursor.moveToFirst()) {
            do {
                units.add(unitsCursor.getInt(0) to unitsCursor.getString(1))
            } while (unitsCursor.moveToNext())
        }

        return units
    }
}
