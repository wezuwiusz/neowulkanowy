package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.sdk.pojo.Device as SdkDevice
import io.github.wulkanowy.sdk.pojo.Token as SdkToken

fun List<SdkDevice>.mapToEntities(student: Student) = map {
    MobileDevice(
        userLoginId = student.userLoginId,
        date = it.createDateZoned.toInstant(),
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
