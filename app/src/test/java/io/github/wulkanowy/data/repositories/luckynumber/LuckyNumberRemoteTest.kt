package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.repositories.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.reactivex.Maybe
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

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
        every { mockSdk.getLuckyNumber("test") } returns Maybe.just(14)

        every { mockSdk.diaryId } returns 1

        val luckyNumber = LuckyNumberRemote(mockSdk)
            .getLuckyNumber(student)
            .blockingGet()

        assertEquals(14, luckyNumber.luckyNumber)
        assertEquals(LocalDate.now(), luckyNumber.date)
        assertEquals(student.studentId, luckyNumber.studentId)
    }
}
