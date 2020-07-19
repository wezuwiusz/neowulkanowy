package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRepository @Inject constructor(
    private val local: MobileDeviceLocal,
    private val remote: MobileDeviceRemote
) {

    fun getDevices(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getDevices(semester) },
        fetch = { remote.getDevices(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteDevices(old uniqueSubtract new)
            local.saveDevices(new uniqueSubtract old)
        }
    )

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice) {
        remote.unregisterDevice(student, semester, device)
        local.deleteDevices(listOf(device))
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return remote.getToken(student, semester)
    }
}
