package io.github.wulkanowy.data.repositories.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Student
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class StudentLocalTest {

    private lateinit var studentLocal: StudentLocal

    private lateinit var testDb: AppDatabase

    private lateinit var sharedHelper: SharedPrefHelper

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        sharedHelper = SharedPrefHelper(context.getSharedPreferences("TEST", Context.MODE_PRIVATE))
        studentLocal = StudentLocal(testDb.studentDao, context)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        studentLocal.saveStudent(Student(email = "test", password = "test123", schoolSymbol = "23", endpoint = "fakelog.cf", loginType = "AUTO", isCurrent = true))
            .blockingGet()

        val student = studentLocal.getCurrentStudent(true).blockingGet()
        assertEquals("23", student.schoolSymbol)
    }
}
