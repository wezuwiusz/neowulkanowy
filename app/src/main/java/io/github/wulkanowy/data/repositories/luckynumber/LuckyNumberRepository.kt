package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote
) {

    fun getLuckyNumber(student: Student, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        shouldFetch = { it == null || forceRefresh },
        query = { local.getLuckyNumber(student, now()) },
        fetch = { remote.getLuckyNumber(student) },
        saveFetchResult = { old, new ->
            if (new != old) {
                old?.let { local.deleteLuckyNumber(it) }
                local.saveLuckyNumber(new?.apply {
                    if (notify) isNotified = false
                })
            }
        }
    )

    fun getNotNotifiedLuckyNumber(student: Student): Flow<LuckyNumber?> {
        return local.getLuckyNumber(student, now())
    }

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber?) {
        local.updateLuckyNumber(luckyNumber)
    }
}
