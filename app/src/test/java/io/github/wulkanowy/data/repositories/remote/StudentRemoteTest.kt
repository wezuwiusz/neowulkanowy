package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.StudentAndParent
import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.api.generic.School
import io.github.wulkanowy.api.login.AccountPermissionException
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import io.github.wulkanowy.api.generic.Student as StudentApi

class StudentRemoteTest {

    @Mock
    private lateinit var mockApi: Vulcan

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
        doNothing().`when`(mockApi).setCredentials(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun testRemoteAll() {
        `when`(mockApi.symbols).thenReturn(mutableListOf("przeworsk", "jaroslaw", "zarzecze"))
        `when`(mockApi.schools).thenReturn(mutableListOf(
                School("ZSTIO", "123", false),
                School("ZSZ", "998", true)))

        val mockSnP = mock(StudentAndParent::class.java)
        `when`(mockSnP.students).thenReturn(mutableListOf(
                StudentApi().apply {
                    id = "20"
                    name = "Włodzimierz"
                    isCurrent = false
                }))
        `when`(mockApi.studentAndParent).thenReturn(mockSnP)

        val students = StudentRemote(mockApi).getConnectedStudents("", "", "").blockingGet()
        assert(students.size == 6)
        assert(students[3].studentName == "Włodzimierz")

    }

    @Test
    fun testOneEmptySymbol() {
        doReturn(mutableListOf("przeworsk")).`when`(mockApi).symbols
        doThrow(AccountPermissionException::class.java).`when`(mockApi).schools

        val students = StudentRemote(mockApi).getConnectedStudents("", "", "").blockingGet()
        assert(students.isEmpty())
    }

    @Test
    fun testDefaultSymbol() {
        doReturn(listOf("Default")).`when`(mockApi).symbols

        val students = StudentRemote(mockApi).getConnectedStudents("", "", "").blockingGet()
        assert(students.isEmpty())
    }
}
