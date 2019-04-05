package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class Migration12Test : AbstractMigrationTest() {

    @Test
    fun twoNotRelatedStudents() {
        helper.createDatabase(dbName, 11).apply {
            // user 1
            createStudent(this, 1, true)
            createSemester(this, 1, false, 5, 1)
            createSemester(this, 1, true, 5, 2)

            // user 2
            createStudent(this, 2, true)
            createSemester(this, 2, false, 6, 1)
            createSemester(this, 2, true, 6, 2)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 12, true, Migration12())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(2, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals(5, classId)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals(6, classId)
        }
    }

    @Test
    fun removeStudentsWithoutClassId() {
        helper.createDatabase(dbName, 11).apply {
            // user 1
            createStudent(this, 1, true)
            createSemester(this, 1, false, 0, 2)
            createStudent(this, 2, true)
            createSemester(this, 2, true, 1, 2)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 12, true, Migration12())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(1, students.size)

        students[0].run {
            assertEquals(2, studentId)
            assertEquals(1, classId)
        }
    }

    @Test
    fun ensureThereIsOnlyOneCurrentStudent() {
        helper.createDatabase(dbName, 11).apply {
            // user 1
            createStudent(this, 1, true)
            createSemester(this, 1, true, 5, 2)
            createStudent(this, 2, true)
            createSemester(this, 2, true, 6, 2)
            createStudent(this, 3, true)
            createSemester(this, 3, false, 7, 2)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 12, true, Migration12())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(3, students.size)

        students[0].run {
            assertEquals(studentId, 1)
            assertEquals(false, isCurrent)
        }
        students[1].run {
            assertEquals(studentId, 2)
            assertEquals(false, isCurrent)
        }
        students[2].run {
            assertEquals(studentId, 3)
            assertEquals(true, isCurrent)
        }
    }

    private fun createStudent(db: SupportSQLiteDatabase, studentId: Int, isCurrent: Boolean) {
        db.insert("Students", CONFLICT_FAIL, ContentValues().apply {
            put("endpoint", "https://fakelog.cf")
            put("loginType", "STANDARD")
            put("email", "jan@fakelog.cf")
            put("password", "******")
            put("symbol", "Default")
            put("student_id", studentId)
            put("student_name", "Jan Kowalski")
            put("school_id", "000123")
            put("school_name", "")
            put("is_current", isCurrent)
            put("registration_date", "0")
        })
    }

    private fun createSemester(db: SupportSQLiteDatabase, studentId: Int, isCurrent: Boolean, classId: Int, diaryId: Int) {
        db.insert("Semesters", CONFLICT_FAIL, ContentValues().apply {
            put("student_id", studentId)
            put("diary_id", diaryId)
            put("diary_name", "IA")
            put("semester_id", diaryId * 5)
            put("semester_name", "1")
            put("is_current", isCurrent)
            put("class_id", classId)
            put("unit_id", "99")
        })
    }
}
