package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.SchoolAnnouncementDao
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolAnnouncementRepository @Inject constructor(
    private val schoolAnnouncementDb: SchoolAnnouncementDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "school_announcement"

    fun getSchoolAnnouncements(
        student: Student,
        forceRefresh: Boolean,
        notify: Boolean = false
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = {
            schoolAnnouncementDb.loadAll(student.studentId)
        },
        fetch = {
            val sdk = wulkanowySdkFactory.create(student)
            val lastAnnouncements = sdk.getLastAnnouncements().mapToEntities(student)
            val directorInformation = sdk.getDirectorInformation().mapToEntities(student)
            lastAnnouncements + directorInformation
        },
        saveFetchResult = { old, new ->
            schoolAnnouncementDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = (new uniqueSubtract old).onEach {
                    if (notify) it.isNotified = false
                },
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )

    fun getSchoolAnnouncementFromDatabase(student: Student): Flow<List<SchoolAnnouncement>> {
        return schoolAnnouncementDb.loadAll(student.studentId)
    }

    suspend fun updateSchoolAnnouncement(schoolAnnouncement: List<SchoolAnnouncement>) =
        schoolAnnouncementDb.updateAll(schoolAnnouncement)
}
