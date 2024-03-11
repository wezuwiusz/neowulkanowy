package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.ConferenceDao
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRepository @Inject constructor(
    private val conferenceDb: ConferenceDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "conference"

    fun getConferences(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        notify: Boolean = false,
        startDate: Instant = Instant.EPOCH,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = {
            conferenceDb.loadAll(semester.diaryId, student.studentId, startDate)
        },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getConferences()
                .mapToEntities(semester)
                .filter { it.date >= startDate }
        },
        saveFetchResult = { old, new ->
            conferenceDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = (new uniqueSubtract old).onEach {
                    if (notify) it.isNotified = false
                },
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )

    fun getConferenceFromDatabase(semester: Semester): Flow<List<Conference>> =
        conferenceDb.loadAll(
            diaryId = semester.diaryId,
            studentId = semester.studentId,
            startDate = Instant.EPOCH,
        )

    suspend fun updateConference(conference: List<Conference>) = conferenceDb.updateAll(conference)
}
