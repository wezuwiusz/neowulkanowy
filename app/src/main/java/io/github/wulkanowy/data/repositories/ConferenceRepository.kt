package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ConferenceDao
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRepository @Inject constructor(
    private val conferenceDb: ConferenceDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "conference"

    fun getConferences(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        notify: Boolean = false
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            it.isEmpty() || forceRefresh
                || refreshHelper.isShouldBeRefreshed(getRefreshKey(cacheKey, semester))
        },
        query = {
            conferenceDb.loadAll(
                semester.diaryId,
                student.studentId
            )
        },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getConferences()
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            val conferencesToSave = (new uniqueSubtract old).onEach {
                if (notify) it.isNotified = false
            }

            conferenceDb.deleteAll(old uniqueSubtract new)
            conferenceDb.insertAll(conferencesToSave)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )

    fun getConferenceFromDatabase(semester: Semester): Flow<List<Conference>> {
        return conferenceDb.loadAll(semester.diaryId, semester.studentId)
    }

    suspend fun updateConference(conference: List<Conference>) = conferenceDb.updateAll(conference)
}
