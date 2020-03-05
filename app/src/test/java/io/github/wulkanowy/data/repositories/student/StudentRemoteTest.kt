package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class StudentRemoteTest {

    @Mock
    private lateinit var mockSdk: Sdk

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRemoteAll() {
        doReturn(Single.just(listOf(getStudent("test")))).`when`(mockSdk).getStudentsFromScrapper(anyString(), anyString(), anyString(), anyString())

        val students = StudentRemote(mockSdk).getStudentsScrapper("", "", "http://fakelog.cf", "").blockingGet()
        assertEquals(1, students.size)
        assertEquals("test", students.first().studentName)
    }

    private fun getStudent(name: String): Student {
        return Student(
            email = "",
            symbol = "",
            studentId = 0,
            userLoginId = 0,
            studentName = name,
            schoolSymbol = "",
            schoolShortName = "",
            schoolName = "",
            className = "",
            classId = 0,
            certificateKey = "",
            privateKey = "",
            loginMode = Sdk.Mode.SCRAPPER,
            mobileBaseUrl = "",
            loginType = Sdk.ScrapperLoginType.STANDARD,
            scrapperBaseUrl = "",
            isParent = false
        )
    }
}
