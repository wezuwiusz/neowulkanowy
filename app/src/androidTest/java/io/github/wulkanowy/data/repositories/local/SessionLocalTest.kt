package io.github.wulkanowy.data.repositories.local

import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Student
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SessionLocalTest {

    private lateinit var sessionLocal: SessionLocal

    private lateinit var testDb: AppDatabase

    private lateinit var sharedHelper: SharedPrefHelper

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getContext()
        testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .build()
        sharedHelper = SharedPrefHelper(context.getSharedPreferences("TEST", Context.MODE_PRIVATE))
        sessionLocal = SessionLocal(testDb.studentDao(), testDb.semesterDao(), sharedHelper, context)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        sessionLocal.saveStudent(Student(email = "test", password = "test123", schoolId = "23")).blockingAwait()
        assert(sharedHelper.getLong(SessionLocal.LAST_USER_KEY, 0) == 1L)

        assert(sessionLocal.isSessionSaved)

        val student = sessionLocal.getLastStudent().blockingGet()
        assertEquals("23", student.schoolId)
    }
}
