package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.github.wulkanowy.data.db.Converters
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate.of
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
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
        val students = runBlocking { db.studentDao.loadAll() }

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

        db.close()
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
        val students = runBlocking { db.studentDao.loadAll() }

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

        db.close()
    }

    @Test
    fun markAtLeastAndOnlyOneSemesterAtCurrent() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "", 5)
            createSemester(this, 1, 1, 1, false)
            createSemester(this, 1, 2, 1, false)
            createSemester(this, 1, 3, 2, false)
            createSemester(this, 1, 4, 2, false)

            createStudent(this, 2, "", 5)
            createSemester(this, 2, 5, 5, true)
            createSemester(this, 2, 6, 5, true)
            createSemester(this, 2, 7, 55, true)
            createSemester(this, 2, 8, 55, true)

            createStudent(this, 3, "", 5)
            createSemester(this, 3, 11, 99, false)
            createSemester(this, 3, 12, 99, false)
            createSemester(this, 3, 13, 100, false)
            createSemester(this, 3, 14, 100, true)
            close()
        }

        val db = helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val semesters1 = getSemesters(db, "SELECT * FROM Semesters WHERE student_id = 1 AND class_id = 5")
        assertTrue { semesters1.single { it.second }.second }
        semesters1[0].run {
            assertFalse(second)
            assertEquals(1, first.semesterId)
            assertEquals(1, first.diaryId)
        }
        semesters1[2].run {
            assertFalse(second)
            assertEquals(3, first.semesterId)
            assertEquals(2, first.diaryId)
        }
        semesters1[3].run {
            assertTrue(second)
            assertEquals(4, first.semesterId)
            assertEquals(2, first.diaryId)
        }

        getSemesters(db, "SELECT * FROM Semesters WHERE student_id = 2 AND class_id = 5").let { semesters ->
            assertTrue { semesters.single { it.second }.second }
            assertEquals(1970, semesters[0].first.schoolYear)
            assertEquals(of(1970, 1, 1), semesters[0].first.end)
            assertEquals(of(1970, 1, 1), semesters[0].first.start)
            assertFalse(semesters[0].second)
            assertFalse(semesters[1].second)
            assertFalse(semesters[2].second)
            assertTrue(semesters[3].second)
        }

        getSemesters(db, "SELECT * FROM Semesters WHERE student_id = 2 AND class_id = 5").let { semesters ->
            assertTrue { semesters.single { it.second }.second }
            assertFalse(semesters[0].second)
            assertFalse(semesters[1].second)
            assertFalse(semesters[2].second)
            assertTrue(semesters[3].second)
        }
        db.close()
    }

    private fun getSemesters(db: SupportSQLiteDatabase, query: String): List<Pair<Semester, Boolean>> {
        val semesters = mutableListOf<Pair<Semester, Boolean>>()

        db.query(query).use {
            if (it.moveToFirst()) {
                do {
                    semesters.add(Semester(
                        studentId = it.getInt(1),
                        diaryId = it.getInt(2),
                        kindergartenDiaryId = 0,
                        diaryName = it.getString(3),
                        semesterId = it.getInt(4),
                        semesterName = it.getInt(5),
                        classId = it.getInt(7),
                        unitId = it.getInt(8),
                        schoolYear = it.getInt(9),
                        start = Converters().timestampToLocalDate(it.getLong(10))!!,
                        end = Converters().timestampToLocalDate(it.getLong(11))!!
                    ) to (it.getInt(6) == 1))
                } while (it.moveToNext())
            }
        }

        return semesters.toList()
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

    private fun createSemester(
        db: SupportSQLiteDatabase,
        studentId: Int,
        semesterId: Int,
        diaryId: Int,
        isCurrent: Boolean = false
    ) {
        db.insert("Semesters", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("student_id", studentId)
            put("diary_id", diaryId)
            put("diary_name", "IA")
            put("semester_id", semesterId)
            put("semester_name", "1")
            put("is_current", isCurrent)
            put("class_id", 5)
            put("unit_id", "99")
        })
    }
}
