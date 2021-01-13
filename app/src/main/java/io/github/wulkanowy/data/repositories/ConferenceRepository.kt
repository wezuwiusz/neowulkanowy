package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ConferenceDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRepository @Inject constructor(
    private val conferenceDb: ConferenceDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "conference"

    fun getConferences(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh || refreshHelper.isShouldBeRefreshed(getRefreshKey(cacheKey, semester)) },
        query = { conferenceDb.loadAll(semester.diaryId, student.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getConferences()
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            conferenceDb.deleteAll(old uniqueSubtract new)
            conferenceDb.insertAll(new uniqueSubtract old)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )
}
