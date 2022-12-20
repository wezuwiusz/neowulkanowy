package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
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

        runMigrationsAndValidate(Migration12())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(2, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals(5, classId)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals(6, classId)
        }
        db.close()
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

        runMigrationsAndValidate(Migration12())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(1, students.size)

        students[0].run {
            assertEquals(2, studentId)
            assertEquals(1, classId)
        }
        db.close()
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

        runMigrationsAndValidate(Migration12())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

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
        db.close()
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
