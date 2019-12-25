package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.Semester
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
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getLuckyNumberTest() {
        every { mockSdk.getLuckyNumber() } returns Maybe.just(14)

        every { mockSdk.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1

        val luckyNumber = LuckyNumberRemote(mockSdk)
            .getLuckyNumber(semesterMock)
            .blockingGet()

        assertEquals(14, luckyNumber.luckyNumber)
        assertEquals(LocalDate.now(), luckyNumber.date)
        assertEquals(semesterMock.studentId, luckyNumber.studentId)
    }
}
