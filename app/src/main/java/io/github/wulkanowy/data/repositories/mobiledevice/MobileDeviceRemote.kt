package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val sdk: Sdk) {

    fun getDevices(student: Student, semester: Semester): Single<List<MobileDevice>> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getRegisteredDevices()
            .map { devices ->
                devices.map {
                    MobileDevice(
                        studentId = semester.studentId,
                        date = it.createDate,
                        deviceId = it.id,
                        name = it.name
                    )
                }
            }
    }

    fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice): Single<Boolean> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .unregisterDevice(device.deviceId)
    }

    fun getToken(student: Student, semester: Semester): Single<MobileDeviceToken> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getToken()
            .map {
                MobileDeviceToken(
                    token = it.token,
                    symbol = it.symbol,
                    pin = it.pin,
                    qr = it.qrCodeImage
                )
            }
    }
}
