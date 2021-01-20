package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.sdk.pojo.Token as SdkToken
import io.github.wulkanowy.sdk.pojo.Device as SdkDevice

fun List<SdkDevice>.mapToEntities(semester: Semester) = map {
    MobileDevice(
        userLoginId = semester.studentId,
        date = it.createDate,
        deviceId = it.id,
        name = it.name
    )
}

fun SdkToken.mapToMobileDeviceToken() = MobileDeviceToken(
    token = token,
    symbol = symbol,
    pin = pin,
    qr = qrCodeImage
)
