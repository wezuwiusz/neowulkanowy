package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class LuckyNumberRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    private val student = getStudentEntity(Sdk.Mode.SCRAPPER)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getLuckyNumberTest() {
        every { mockSdk.init(student) } returns mockSdk
        coEvery { mockSdk.getLuckyNumber("test") } returns 14

        every { mockSdk.diaryId } returns 1

        val luckyNumber = runBlocking {
            LuckyNumberRemote(mockSdk)
                .getLuckyNumber(student)
        }

        assertEquals(14, luckyNumber?.luckyNumber)
        assertEquals(LocalDate.now(), luckyNumber?.date)
        assertEquals(student.studentId, luckyNumber?.studentId)
    }
}
