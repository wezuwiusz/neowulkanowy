package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getLuckyNumber(student: Student): LuckyNumber? {
        return sdk.init(student).getLuckyNumber(student.schoolShortName)?.let {
            LuckyNumber(
                studentId = student.studentId,
                date = LocalDate.now(),
                luckyNumber = it
            )
        }
    }
}
