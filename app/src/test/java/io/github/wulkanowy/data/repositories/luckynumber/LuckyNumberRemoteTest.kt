package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Maybe
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class LuckyNumberRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    @MockK
    private lateinit var studentMock: Student

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getLuckyNumberTest() {
        every { mockSdk.getLuckyNumber("test") } returns Maybe.just(14)

        every { mockSdk.diaryId } returns 1
        every { studentMock.studentId } returns 1
        every { studentMock.schoolShortName } returns "test"

        val luckyNumber = LuckyNumberRemote(mockSdk)
            .getLuckyNumber(studentMock)
            .blockingGet()

        assertEquals(14, luckyNumber.luckyNumber)
        assertEquals(LocalDate.now(), luckyNumber.date)
        assertEquals(studentMock.studentId, luckyNumber.studentId)
    }
}
