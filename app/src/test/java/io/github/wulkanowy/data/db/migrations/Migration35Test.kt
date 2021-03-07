package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.github.wulkanowy.utils.AppInfo
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class Migration35Test : AbstractMigrationTest() {

    @Test
    fun addRandomAvatarColorsForStudents() {
        with(helper.createDatabase(dbName, 34)) {
            createStudent(this, 1)
            createStudent(this, 2)
            close()
        }

        helper.runMigrationsAndValidate(dbName, 35, true, Migration35(AppInfo()))

        val db = getMigratedRoomDatabase()
        val students = runBlocking { db.studentDao.loadAll() }

        assertEquals(2, students.size)

        assertTrue { students[0].avatarColor in AppInfo().defaultColorsForAvatar }
        assertTrue { students[1].avatarColor in AppInfo().defaultColorsForAvatar }
    }

    private fun createStudent(db: SupportSQLiteDatabase, id: Long) {
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
            put("user_login_id", Random.nextInt())
            put("student_name", "")
            put("user_name", "")
            put("nick", "")
        })
    }
}
