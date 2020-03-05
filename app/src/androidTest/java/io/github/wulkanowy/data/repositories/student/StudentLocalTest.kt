package io.github.wulkanowy.data.repositories.student

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime.now
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class StudentLocalTest {

    private lateinit var studentLocal: StudentLocal

    private lateinit var testDb: AppDatabase

    private lateinit var sharedProvider: SharedPrefProvider

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        sharedProvider = SharedPrefProvider(context.getSharedPreferences("TEST", Context.MODE_PRIVATE))
        studentLocal = StudentLocal(testDb.studentDao, context)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        studentLocal.saveStudents(listOf(Student(email = "test", password = "test123", schoolSymbol = "23", scrapperBaseUrl = "fakelog.cf", loginType = "AUTO", isCurrent = true, studentName = "", schoolShortName = "", schoolName = "", studentId = 0, classId = 1, symbol = "", registrationDate = now(), className = "", loginMode = "API", certificateKey = "", privateKey = "", mobileBaseUrl = "", userLoginId = 0, isParent = false)))
            .blockingGet()

        val student = studentLocal.getCurrentStudent(true).blockingGet()
        assertEquals("23", student.schoolSymbol)
    }
}
