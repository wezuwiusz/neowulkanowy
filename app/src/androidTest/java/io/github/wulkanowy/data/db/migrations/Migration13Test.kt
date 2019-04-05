package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.threeten.bp.LocalDate.of
import kotlin.test.assertTrue

class Migration13Test : AbstractMigrationTest() {

    @Test
    fun studentsWithSchoolNameWithClassName() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "Klasa A - Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", 1, 1)
            createStudent(this, 2, "Klasa B - Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", 2, 1)
            createStudent(this, 2, "Klasa C - Publiczna szkoła Wulkanowego-fejka nr 2 w fakelog.cf", 1, 2)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(3, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals("A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals("B", className)
            assertEquals("Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", schoolName)
        }

        students[2].run {
            assertEquals(2, studentId)
            assertEquals("C", className)
            assertEquals("Publiczna szkoła Wulkanowego-fejka nr 2 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun studentsWithSchoolNameWithoutClassName() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", 1)
            createStudent(this, 2, "Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", 1)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(2, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals("", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals("", className)
            assertEquals("Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun markAtLeastAndOnlyOneSemesterAtCurrent() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "", 5)
            createSemester(this, 1, 5, 1, 1, false)
            createSemester(this, 1, 5, 2, 1, false)
            createSemester(this, 1, 5, 3, 2, false)
            createSemester(this, 1, 5, 4, 2, false)

            createStudent(this, 2, "", 5)
            createSemester(this, 2, 5, 5, 5, true)
            createSemester(this, 2, 5, 6, 5, true)
            createSemester(this, 2, 5, 7, 55, true)
            createSemester(this, 2, 5, 8, 55, true)

            createStudent(this, 3, "", 5)
            createSemester(this, 3, 5, 11, 99, false)
            createSemester(this, 3, 5, 12, 99, false)
            createSemester(this, 3, 5, 13, 100, false)
            createSemester(this, 3, 5, 14, 100, true)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val db = getMigratedRoomDatabase()

        val semesters1 = db.semesterDao.loadAll(1, 5).blockingGet()
        assertTrue { semesters1.single { it.isCurrent }.isCurrent }
        semesters1[0].run {
            assertFalse(isCurrent)
            assertEquals(1, semesterId)
            assertEquals(1, diaryId)
        }
        semesters1[2].run {
            assertFalse(isCurrent)
            assertEquals(3, semesterId)
            assertEquals(2, diaryId)
        }
        semesters1[3].run {
            assertTrue(isCurrent)
            assertEquals(4, semesterId)
            assertEquals(2, diaryId)
        }

        db.semesterDao.loadAll(2, 5).blockingGet().let {
            assertTrue { it.single { it.isCurrent }.isCurrent }
            assertEquals(1970, it[0].schoolYear)
            assertEquals(of(1970, 1, 1), it[0].end)
            assertEquals(of(1970, 1, 1), it[0].start)
            assertFalse(it[0].isCurrent)
            assertFalse(it[1].isCurrent)
            assertFalse(it[2].isCurrent)
            assertTrue(it[3].isCurrent)
        }

        db.semesterDao.loadAll(2, 5).blockingGet().let {
            assertTrue { it.single { it.isCurrent }.isCurrent }
            assertFalse(it[0].isCurrent)
            assertFalse(it[1].isCurrent)
            assertFalse(it[2].isCurrent)
            assertTrue(it[3].isCurrent)
        }
    }

    private fun createStudent(db: SupportSQLiteDatabase, studentId: Int, schoolName: String = "", classId: Int = -1, schoolId: Int = 123) {
        db.insert("Students", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("endpoint", "https://fakelog.cf")
            put("loginType", "STANDARD")
            put("email", "jan@fakelog.cf")
            put("password", "******")
            put("symbol", "Default")
            put("student_id", studentId)
            put("class_id", classId)
            put("student_name", "Jan Kowalski")
            put("school_id", schoolId)
            put("school_name", schoolName)
            put("is_current", false)
            put("registration_date", "0")
        })
    }

    private fun createSemester(db: SupportSQLiteDatabase, studentId: Int, classId: Int, semesterId: Int, diaryId: Int, isCurrent: Boolean = false) {
        db.insert("Semesters", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("student_id", studentId)
            put("diary_id", diaryId)
            put("diary_name", "IA")
            put("semester_id", semesterId)
            put("semester_name", "1")
            put("is_current", isCurrent)
            put("class_id", classId)
            put("unit_id", "99")
        })
    }
}
