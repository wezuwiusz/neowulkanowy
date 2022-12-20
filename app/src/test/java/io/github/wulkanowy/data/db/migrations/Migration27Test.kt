package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class Migration27Test : AbstractMigrationTest() {

    @Test
    fun userWithoutCorrespondingUnit() {
        with(helper.createDatabase(dbName, 26)) {
            createStudent(this, 321, 123, "Jan Student")
            createUnit(this, 9999, "Unit Jan")
            close()
        }

        runMigrationsAndValidate(Migration27())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(1, students.size)

        with(students[0]) {
            assertEquals(321, id)
            assertEquals(123, userLoginId)
            assertEquals("Student Jan", userName)
        }

        db.close()
    }

    @Test
    fun userWithCorrespondingUnit() {
        with(helper.createDatabase(dbName, 26)) {
            createStudent(this, 1, 2, "Jan Kowalski Student")
            createUnit(this, 2, "Unit Jan")
            close()
        }

        runMigrationsAndValidate(Migration27())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(1, students.size)

        with(students[0]) {
            assertEquals(1, id)
            assertEquals(2, userLoginId)
            assertEquals("Unit Jan", userName)
        }

        db.close()
    }

    @Test
    fun studentAccountAndParentAccountWithCorrespondingUnits() {
        with(helper.createDatabase(dbName, 26)) {
            createStudent(this, 1, 222, "Jan Student")
            createStudent(this, 2, 333, "Jan Parent")
            createUnit(this, 222, "Unit Jan")
            createUnit(this, 333, "Unit Tomasz")
            close()
        }

        runMigrationsAndValidate(Migration27())

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(2, students.size)

        with(students[0]) {
            assertEquals(1, id)
            assertEquals(222, userLoginId)
            assertEquals("Unit Jan", userName)
        }
        with(students[1]) {
            assertEquals(2, id)
            assertEquals(333, userLoginId)
            assertEquals("Unit Tomasz", userName)
        }

        db.close()
    }

    private fun createStudent(db: SupportSQLiteDatabase, id: Long, userLoginId: Int, studentName: String) {
        db.insert("Students", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("id", id)
            put("scrapper_base_url", "https://fakelog.cf")
            put("mobile_base_url", "")
            put("login_mode", "SCRAPPER")
            put("login_type", "STANDARD")
            put("certificate_key", "")
            put("private_key", "")
            put("is_parent", false)
            put("email", "jan@fakelog.cf")
            put("password", "******")
            put("symbol", "Default")
            put("school_short", "")
            put("class_name", "")
            put("student_id", Random.nextInt())
            put("class_id", Random.nextInt())
            put("school_id", "123")
            put("school_name", "Wulkan first class school")
            put("is_current", false)
            put("registration_date", "0")

            put("user_login_id", userLoginId)
            put("student_name", studentName)
        })
    }

    private fun createUnit(db: SupportSQLiteDatabase, senderId: Int, senderName: String) {
        db.insert("ReportingUnits", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("student_id", Random.nextInt())
            put("real_id", Random.nextInt())
            put("short", "SHORT")
            put("roles", "[0]")

            put("sender_id", senderId)
            put("sender_name", senderName)
        })
    }
}
