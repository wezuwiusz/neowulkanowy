package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRemote @Inject constructor(private val sdk: Sdk) {

    fun getLuckyNumber(semester: Semester): Maybe<LuckyNumber> {
        return sdk.getLuckyNumber()
            .map {
                LuckyNumber(
                    studentId = semester.studentId,
                    date = LocalDate.now(),
                    luckyNumber = it
                )
            }
    }
}
