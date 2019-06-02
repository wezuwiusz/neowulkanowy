package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceLocal @Inject constructor(private val mobileDb: MobileDeviceDao) {

    fun saveDevices(devices: List<MobileDevice>) {
        mobileDb.insertAll(devices)
    }

    fun deleteDevices(devices: List<MobileDevice>) {
        mobileDb.deleteAll(devices)
    }

    fun getDevices(semester: Semester): Maybe<List<MobileDevice>> {
        return mobileDb.loadAll(semester.studentId).filter { it.isNotEmpty() }
    }
}
