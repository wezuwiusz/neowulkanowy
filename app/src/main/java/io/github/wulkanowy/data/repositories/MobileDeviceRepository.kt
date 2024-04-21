package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.mappers.mapToMobileDeviceToken
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRepository @Inject constructor(
    private val mobileDb: MobileDeviceDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
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
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { mobileDb.loadAll(student.studentId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getRegisteredDevices()
                .mapToEntities(student)
        },
        saveFetchResult = { old, new ->
            mobileDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = new uniqueSubtract old,
            )
            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
        }
    )

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice) {
        wulkanowySdkFactory.create(student, semester)
            .unregisterDevice(device.deviceId)

        mobileDb.deleteAll(listOf(device))
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return wulkanowySdkFactory.create(student, semester)
            .getToken()
            .mapToMobileDeviceToken()
    }
}
