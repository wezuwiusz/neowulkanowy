package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val luckyNumberDb: LuckyNumberDao,
    private val sdk: Sdk
) {

    fun getLuckyNumber(student: Student, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        shouldFetch = { it == null || forceRefresh },
        query = { luckyNumberDb.load(student.studentId, now()) },
        fetch = { sdk.init(student).getLuckyNumber(student.schoolShortName)?.mapToEntity(student) },
        saveFetchResult = { old, new ->
            if (new != old) {
                old?.let { luckyNumberDb.deleteAll(listOfNotNull(it)) }
                luckyNumberDb.insertAll(listOfNotNull((new?.apply {
                    if (notify) isNotified = false
                })))
            }
        }
    )

    suspend fun getNotNotifiedLuckyNumber(student: Student) = luckyNumberDb.load(student.studentId, now()).map {
        if (it?.isNotified == false) it else null
    }.first()

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber?) = luckyNumberDb.updateAll(listOfNotNull(luckyNumber))
}
