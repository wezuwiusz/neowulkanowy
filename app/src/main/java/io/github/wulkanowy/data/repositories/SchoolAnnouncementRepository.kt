package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.SchoolAnnouncementDao
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
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
import kotlinx.coroutines.flow.map
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

    fun getSchoolAnnouncements(
        student: Student,
        forceRefresh: Boolean,
        notify: Boolean = false
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            it.isEmpty() || forceRefresh
                || refreshHelper.isShouldBeRefreshed(getRefreshKey(cacheKey, student))
        },
        query = {
            schoolAnnouncementDb.loadAll(
                student.studentId)
        },
        fetch = {
            sdk.init(student)
                .getDirectorInformation()
                .mapToEntities(student)
        },
        saveFetchResult = { old, new ->
            val schoolAnnouncementsToSave = (new uniqueSubtract old).onEach {
                if (notify) it.isNotified = false
            }

            schoolAnnouncementDb.deleteAll(old uniqueSubtract new)
            schoolAnnouncementDb.insertAll(schoolAnnouncementsToSave)
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )
    fun getNotNotifiedSchoolAnnouncement(semester: Semester): Flow<List<SchoolAnnouncement>> {
        return schoolAnnouncementDb.loadAll(
            studentId = semester.studentId
        ).map {
            it.filter { schoolAnnouncement -> !schoolAnnouncement.isNotified }
        }
    }

    suspend fun updateSchoolAnnouncement(schoolAnnouncement: List<SchoolAnnouncement>) = schoolAnnouncementDb.updateAll(schoolAnnouncement)
}
