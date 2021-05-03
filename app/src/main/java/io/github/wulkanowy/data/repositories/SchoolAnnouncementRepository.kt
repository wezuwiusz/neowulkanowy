package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.SchoolAnnouncementDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolAnnouncementRepository @Inject constructor(
    private val schoolAnnouncementDb: SchoolAnnouncementDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "school_announcement"

    fun getSchoolAnnouncements(student: Student, forceRefresh: Boolean) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            it.isEmpty() || forceRefresh || refreshHelper.isShouldBeRefreshed(
                getRefreshKey(cacheKey, student)
            )
        },
        query = { schoolAnnouncementDb.loadAll(student.studentId) },
        fetch = { sdk.init(student).getDirectorInformation().mapToEntities(student) },
        saveFetchResult = { old, new ->
            schoolAnnouncementDb.deleteAll(old uniqueSubtract new)
            schoolAnnouncementDb.insertAll(new uniqueSubtract old)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )
}
