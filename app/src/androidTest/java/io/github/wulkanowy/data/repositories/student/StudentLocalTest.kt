package io.github.wulkanowy.data.repositories.student

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.repositories.getStudent
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class StudentLocalTest {

    private lateinit var studentLocal: StudentLocal

    private lateinit var testDb: AppDatabase

    private val student = getStudent()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        studentLocal = StudentLocal(testDb.studentDao, context)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        studentLocal.saveStudents(listOf(student)).blockingGet()

        val student = studentLocal.getCurrentStudent(true).blockingGet()
        assertEquals("23", student.schoolSymbol)
    }
}
