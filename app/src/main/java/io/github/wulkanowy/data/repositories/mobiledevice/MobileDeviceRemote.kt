package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getDevices(student: Student, semester: Semester): List<MobileDevice> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getRegisteredDevices()
            .map {
                MobileDevice(
                    studentId = semester.studentId,
                    date = it.createDate,
                    deviceId = it.id,
                    name = it.name
                )
            }
    }

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice): Boolean {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .unregisterDevice(device.deviceId)
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getToken()
            .let {
                MobileDeviceToken(
                    token = it.token,
                    symbol = it.symbol,
                    pin = it.pin,
                    qr = it.qrCodeImage
                )
            }
    }
}
