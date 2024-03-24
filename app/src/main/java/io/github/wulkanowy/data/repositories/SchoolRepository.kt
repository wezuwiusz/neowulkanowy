package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntity
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val schoolDb: SchoolDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "school_info"

    fun getSchoolInfo(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it == null },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(cacheKey, student)
            )
            it == null || forceRefresh || isExpired
        },
        query = { schoolDb.load(semester.studentId, semester.classId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getSchool()
                .mapToEntity(semester)
        },
        saveFetchResult = { old, new ->
            if (old != null && new != old) {
                schoolDb.removeOldAndSaveNew(
                    oldItems = listOf(old),
                    newItems = listOf(new)
                )
            } else if (old == null) {
                schoolDb.insertAll(listOf(new))
            }
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )
}
