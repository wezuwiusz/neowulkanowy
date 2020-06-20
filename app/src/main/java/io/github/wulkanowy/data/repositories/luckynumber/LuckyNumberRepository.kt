package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import org.threeten.bp.LocalDate.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote
) {

    suspend fun getLuckyNumber(student: Student, forceRefresh: Boolean = false, notify: Boolean = false): LuckyNumber? {
        return local.getLuckyNumber(student, now())?.takeIf { !forceRefresh } ?: run {
            val new = remote.getLuckyNumber(student)
            val old = local.getLuckyNumber(student, now())

            if (new != old) {
                old?.let { local.deleteLuckyNumber(it) }
                local.saveLuckyNumber(new?.apply {
                    if (notify) isNotified = false
                })
            }

            local.saveLuckyNumber(new?.apply {
                if (notify) isNotified = false
            })

            local.getLuckyNumber(student, now())
        }
    }

    suspend fun getNotNotifiedLuckyNumber(student: Student): LuckyNumber? {
        return local.getLuckyNumber(student, now())
    }

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber) {
        local.updateLuckyNumber(luckyNumber)
    }
}
