package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class Migration63Test : AbstractMigrationTest() {

    @Test
    fun `update is_edu_one to null if 0`() = runTest {
        with(helper.createDatabase(dbName, 62)) {
            createStudent(1, 0)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 63, true)

        val database = getMigratedRoomDatabase()
        val studentDb = database.studentDao
        val student = studentDb.loadById(1)

        assertNull(student!!.isEduOne)

        database.close()
    }

    @Test
    fun `check is_edu_one is stay same`() = runTest {
        with(helper.createDatabase(dbName, 62)) {
            createStudent(1, 1)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 63, true)

        val database = getMigratedRoomDatabase()
        val studentDb = database.studentDao
        val student = studentDb.loadById(1)

        val isEduOne = assertNotNull(student!!.isEduOne)
        assertTrue(isEduOne)
        database.close()
    }

    private fun SupportSQLiteDatabase.createStudent(id: Long, isEduOneValue: Int) {
        insert("Students", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("scrapper_base_url", "https://fakelog.cf")
            put("mobile_base_url", "")
            put("login_type", "SCRAPPER")
            put("login_mode", "SCRAPPER")
            put("certificate_key", "")
            put("private_key", "")
            put("is_parent", false)
            put("email", "jan@fakelog.cf")
            put("password", "******")
            put("symbol", "symbol")
            put("student_id", Random.nextInt())
            put("user_login_id", 123)
            put("user_name", "studentName")
            put("student_name", "studentName")
            put("school_id", "123")
            put("school_short", "")
            put("school_name", "")
            put("class_name", "")
            put("class_id", Random.nextInt())
            put("is_current", false)
            put("registration_date", "0")
            put("id", id)
            put("nick", "")
            put("avatar_color", "")
            put("is_edu_one", isEduOneValue)
        })
    }
}
