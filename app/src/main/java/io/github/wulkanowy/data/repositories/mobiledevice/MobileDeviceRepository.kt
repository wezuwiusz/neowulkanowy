package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRepository @Inject constructor(
    private val local: MobileDeviceLocal,
    private val remote: MobileDeviceRemote
) {

    suspend fun getDevices(student: Student, semester: Semester, forceRefresh: Boolean = false): List<MobileDevice> {
        return local.getDevices(semester).filter { !forceRefresh }.ifEmpty {
            val new = remote.getDevices(student, semester)
            val old = local.getDevices(semester)

            local.deleteDevices(old uniqueSubtract new)
            local.saveDevices(new uniqueSubtract old)

            local.getDevices(semester)
        }
    }

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice): Boolean {
        return remote.unregisterDevice(student, semester, device)
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return remote.getToken(student, semester)
    }
}
