package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.mappers.mapToMobileDeviceToken
import io.github.wulkanowy.data.pojos.MobileDeviceToken
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
class MobileDeviceRepository @Inject constructor(
    private val mobileDb: MobileDeviceDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "devices"

    fun getDevices(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { mobileDb.loadAll(student.userLoginId.takeIf { it != 0 } ?: student.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getRegisteredDevices()
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            mobileDb.deleteAll(old uniqueSubtract new)
            mobileDb.insertAll(new uniqueSubtract old)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice) {
        sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .unregisterDevice(device.deviceId)

        mobileDb.deleteAll(listOf(device))
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getToken()
            .mapToMobileDeviceToken()
    }
}
