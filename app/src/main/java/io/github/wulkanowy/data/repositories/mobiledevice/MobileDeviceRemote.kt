package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val api: Api) {

    fun getDevices(semester: Semester): Single<List<MobileDevice>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.getRegisteredDevices() }
            .map { devices ->
                devices.map {
                    MobileDevice(
                        studentId = semester.studentId,
                        date = it.date.toLocalDateTime(),
                        deviceId = it.id,
                        name = it.name
                    )
                }
            }
    }

    fun unregisterDevice(semester: Semester, device: MobileDevice): Single<Boolean> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.unregisterDevice(device.deviceId) }
    }

    fun getToken(semester: Semester): Single<MobileDeviceToken> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.getToken() }
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
