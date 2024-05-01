package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntity
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider
import io.github.wulkanowy.utils.AppWidgetUpdater
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import java.time.LocalDate
import java.time.LocalDate.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val luckyNumberDb: LuckyNumberDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val appWidgetUpdater: AppWidgetUpdater,
) {

    private val saveFetchResultMutex = Mutex()

    fun getLuckyNumber(
        student: Student,
        forceRefresh: Boolean,
        notify: Boolean = false,
        isFromAppWidget: Boolean = false
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it == null },
        shouldFetch = { it == null || forceRefresh },
        query = { luckyNumberDb.load(student.studentId, now()) },
        fetch = {
            wulkanowySdkFactory.create(student)
                .getLuckyNumber(student.schoolShortName)
                ?.mapToEntity(student)
        },
        saveFetchResult = { oldLuckyNumber, newLuckyNumber ->
            newLuckyNumber ?: return@networkBoundResource

            if (newLuckyNumber != oldLuckyNumber) {
                luckyNumberDb.removeOldAndSaveNew(
                    oldItems = listOfNotNull(oldLuckyNumber),
                    newItems = listOf(newLuckyNumber.apply { if (notify) isNotified = false }),
                )
                if (!isFromAppWidget) {
                    appWidgetUpdater.updateAllAppWidgetsByProvider(LuckyNumberWidgetProvider::class)
                }
            }
        }
    )

    fun getLuckyNumberHistory(student: Student, start: LocalDate, end: LocalDate) =
        luckyNumberDb.getAll(student.studentId, start, end)

    suspend fun getNotNotifiedLuckyNumber(student: Student) =
        luckyNumberDb.load(student.studentId, now()).map {
            if (it?.isNotified == false) it else null
        }.first()

    suspend fun updateLuckyNumber(luckyNumber: LuckyNumber?) =
        luckyNumberDb.updateAll(listOfNotNull(luckyNumber))
}
