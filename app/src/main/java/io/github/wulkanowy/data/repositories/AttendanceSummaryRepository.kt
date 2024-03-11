package io.github.wulkanowy.data.repositories

import androidx.room.withTransaction
import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val attendanceDb: AttendanceSummaryDao,
    private val refreshHelper: AutoRefreshHelper,
    private val appDatabase: AppDatabase,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "attendance_summary"

    fun getAttendanceSummary(
        student: Student,
        semester: Semester,
        subjectId: Int,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, semester))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { attendanceDb.loadAll(semester.diaryId, semester.studentId, subjectId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getAttendanceSummary(subjectId)
                .mapToEntities(semester, subjectId)
        },
        saveFetchResult = { old, new ->
            appDatabase.withTransaction {
                attendanceDb.deleteAll(old uniqueSubtract new)
                attendanceDb.insertAll(new uniqueSubtract old)
            }
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )
}
