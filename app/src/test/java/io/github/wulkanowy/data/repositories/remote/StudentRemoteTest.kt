package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.register.Pupil
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class StudentRemoteTest {

    @Mock
    private lateinit var mockApi: Api

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRemoteAll() {
        doReturn(Single.just(listOf(Pupil("", "", "", "test", "", ""))))
                .`when`(mockApi).getPupils()

        val students = SessionRemote(mockApi).getConnectedStudents("", "", "").blockingGet()
        assertEquals(1, students.size)
        assertEquals("test", students.first().studentName)
    }
}
